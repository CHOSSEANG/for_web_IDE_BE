package fs16.webide.web_ide_for.codeRunning.service;

import java.io.InputStream;
import java.util.Properties;

import org.springframework.stereotype.Service;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

@Service
public class CodeRunningService {
	public String executeCommand(String command) {
		// --- 1. 설정 정보 (테스트용으로 직접 입력) ---
		String host = "ec2-3-25-153-25.ap-southeast-2.compute.amazonaws.com"; // EC2 퍼블릭 IP
		String user = "ubuntu";             // EC2 사용자 (Ubuntu는 ubuntu, Amazon Linux는 ec2-user)
		String privateKeyPath = "/Users/chosseang/Downloads/webic_code_server.pem"; // 로컬에 있는 키 경로 (Windows 예시)

		JSch jsch = new JSch();
		Session session = null;
		ChannelExec channel = null;
		StringBuilder output = new StringBuilder();

		try {
			// --- 2. SSH 키(Identity) 등록 ---
			jsch.addIdentity(privateKeyPath);

			// --- 3. 세션 설정 및 연결 ---
			session = jsch.getSession(user, host, 22);

			// 테스트 단계이므로 호스트 키 검사 생략
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);

			session.connect(10000); // 타임아웃 10초

			// --- 4. 명령어 실행 채널 오픈 ---
			channel = (ChannelExec) session.openChannel("exec");
			channel.setCommand(command);

			// 명령어 실행 결과 스트림 받기
			InputStream in = channel.getInputStream();
			InputStream err = channel.getErrStream(); // 에러 로그도 받기 위함

			channel.connect();

			// --- 5. 결과 읽기 (실시간 데이터 처리) ---
			byte[] tmp = new byte[1024];
			while (true) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);
					if (i < 0) break;
					output.append(new String(tmp, 0, i));
				}
				if (channel.isClosed()) {
					if (in.available() > 0) continue;
					break;
				}
				Thread.sleep(100); // CPU 점유율 과부하 방지
			}

		} catch (Exception e) {
			return "Execution Error: " + e.getMessage();
		} finally {
			if (channel != null) channel.disconnect();
			if (session != null) session.disconnect();
		}

		return output.toString();
	}
}
