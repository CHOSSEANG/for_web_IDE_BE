package fs16.webide.web_ide_for.codeRunning.service;

import java.io.InputStream;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import fs16.webide.web_ide_for.common.error.CoreException;
import fs16.webide.web_ide_for.file.entity.ContainerFile;
import fs16.webide.web_ide_for.file.error.FileErrorCode;
import fs16.webide.web_ide_for.file.repository.FileRepository;
import fs16.webide.web_ide_for.file.service.S3FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CodeRunningService {

	private final FileRepository fileRepository;
	private final S3FileService s3FileService;


	// 기존 S3 설정값들을 그대로 가져옵니다.
	@Value("${cloud.aws.credentials.access-key}")
	private String accessKey;

	@Value("${cloud.aws.credentials.secret-key}")
	private String secretKey;

	@Value("${cloud.aws.region.static}")
	private String region;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	private final String host = "ec2-13-239-107-76.ap-southeast-2.compute.amazonaws.com";
	private final String user = "ubuntu";
	private final String pemPath = "/Users/chosseang/Downloads/webic_code_server.pem";

	/**
	* [기존 로직] 단순 쉘 명령어 실행 (/code/command 용)
     */
	public String executeCommand(String command) {
		log.info("단순 명령어 실행: {}", command);
		return executeSsh(command);
	}

	public String runS3FileOnEc2(Long userId, Long containerId, Long fileId) {
		// 1. DB에서 파일 조회
		ContainerFile containerFile = fileRepository.findById(fileId)
			.orElseThrow(() -> new CoreException(FileErrorCode.FILE_NOT_FOUND));

		// 2. S3 Key 생성 (S3FileService 로직 반영)
		String s3Key = generateS3Key(containerFile);
		String fileName = containerFile.getName();
		String workDir = "/home/ubuntu/temp/" + userId + "/";

		// 3. 언어별 명령어 설정
		String language = determineLanguage(containerFile);
		String runCmd = getRunCommand(language, workDir, fileName);

		// 4. AWS 인증 환경 변수 설정 (EC2에 키를 저장하지 않는 방식)
		String awsEnv = String.format("AWS_ACCESS_KEY_ID=%s AWS_SECRET_ACCESS_KEY=%s AWS_DEFAULT_REGION=%s",
			accessKey, secretKey, region);

		// 5. 전체 실행 스크립트 구성
		String fullCommand = String.format(
			"mkdir -p %s && " +
				"%s aws s3 cp s3://%s/%s %s%s --quiet && " + // --quiet 추가
				"cd %s && %s; " +                            // 해당 디렉토리로 이동 후 실행
				"cd ~ && rm -rf %s",                         // 상위로 이동 후 삭제
			workDir, awsEnv, bucket, s3Key, workDir, fileName, workDir, runCmd, workDir
		);

		log.info("EC2 실행 요청 (순수 결과 모드)");
		String rawOutput = executeSsh(fullCommand);

		return rawOutput.trim();
	}

	// 기존 S3FileService.java의 로직 그대로 구현
	private String generateS3Key(ContainerFile file) {
		String path = file.getPath();
		String cleanPath = path.startsWith("/") ? path.substring(1) : path;
		return file.getContainerId() + "/" + cleanPath;
	}

	private String determineLanguage(ContainerFile file) {
		// 파일 확장자나 이름을 보고 언어 판별 (기본값 java)
		String name = file.getName().toLowerCase(); // 소문자로 변환하여 비교
		if (name.endsWith(".py")) return "python";
		if (name.endsWith(".js")) return "javascript";
		if (name.endsWith(".java")) return "java";
		return "java"; // 기본값
	}

	private String getRunCommand(String lang, String dir, String file) {
		return switch (lang.toLowerCase()) {
			case "java" -> {
				// .java 확장자를 확실히 제거한 클래스명 추출
				String className = file.endsWith(".java") ? file.substring(0, file.lastIndexOf(".java")) : file;
				yield String.format("javac %s && java %s", file, className);
			}
			case "python" -> String.format("python3 %s", file);
			case "javascript", "js" -> String.format("node %s", file);
			default -> "echo '지원하지 않는 언어입니다.'";
		};
	}

	private String executeSsh(String command) {
		JSch jsch = new JSch();
		StringBuilder output = new StringBuilder();
		try {
			jsch.addIdentity(pemPath);
			Session session = jsch.getSession(user, host, 22);
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect(10000);

			ChannelExec channel = (ChannelExec) session.openChannel("exec");
			channel.setCommand(command);

			InputStream in = channel.getInputStream();
			InputStream err = channel.getErrStream();
			channel.connect();

			byte[] tmp = new byte[1024];
			while (true) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);
					output.append(new String(tmp, 0, i));
				}
				while (err.available() > 0) {
					int i = err.read(tmp, 0, 1024);
					output.append("[System Error] ").append(new String(tmp, 0, i));
				}
				if (channel.isClosed()) break;
				Thread.sleep(100);
			}
			channel.disconnect();
			session.disconnect();
		} catch (Exception e) {
			log.error("SSH 실행 에러", e);
			return "Error: " + e.getMessage();
		}
		return output.toString();
	}
}
