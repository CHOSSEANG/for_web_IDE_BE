package fs16.webide.web_ide_for.common;

import fs16.webide.web_ide_for.common.error.ErrorMessage;
import io.micrometer.common.lang.Nullable;

/**
 * API 응답 표준 형식을 정의하는 레코드.
 * 성공 데이터(data) 또는 오류 정보(error) 중 하나만 가질 수 있도록 설계되었습니다.
 *
 * @param <T> 성공 시 반환될 데이터의 타입
 */
public record ApiResponse<T>(
	@Nullable T data,
	@Nullable ErrorMessage error
) {

	/**
	 * 성공 응답을 생성하는 정적 팩토리 메서드.
	 * @param data 클라이언트에게 반환할 데이터
	 * @param <S> 데이터 타입
	 * @return 성공 응답을 담은 ApiResponse 인스턴스
	 */
	public static <S> ApiResponse<S> success(S data) {
		return new ApiResponse<>(data, null);
	}

	/**
	 * 오류 응답을 생성하는 정적 팩토리 메서드.
	 * @param errorMessage 오류 정보를 담고 있는 ErrorMessage 객체
	 * @return 오류 응답을 담은 ApiResponse 인스턴스 (데이터 타입은 Void로 처리)
	 */
	public static ApiResponse<Void> error(ErrorMessage errorMessage) {
		return new ApiResponse<>(null, errorMessage);
	}
}
