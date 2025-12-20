package fs16.webide.web_ide_for.file.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileCreateRequest {
    private Long containerId;
    private String name;    // 이 이름을 통해 확장자와 디렉토리 여부를 판단합니다.
    private Long parentId;
    private String content; // 파일 생성 시 내용을 포함할 수 있음
}
