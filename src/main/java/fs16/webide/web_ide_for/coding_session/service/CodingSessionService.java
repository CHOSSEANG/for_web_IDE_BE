package fs16.webide.web_ide_for.coding_session.service;

import fs16.webide.web_ide_for.coding_session.dto.CodingTimeRequestDto;
import fs16.webide.web_ide_for.coding_session.dto.DailyCodingTimeDto;
import fs16.webide.web_ide_for.coding_session.dto.WeeklyCodingTimeDto;
import fs16.webide.web_ide_for.coding_session.entity.CodingSession;
import fs16.webide.web_ide_for.coding_session.repository.CodingSessionRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class CodingSessionService{

    private final CodingSessionRepository codingSessionRepository;

    public CodingSessionService(CodingSessionRepository codingSessionRepository) {
        this.codingSessionRepository = codingSessionRepository;
    }

    // 전송 데이터
    public WeeklyCodingTimeDto weeklyCodingTime(Long userId){
        // 일별 코딩 시간 조회
        LocalDate today = LocalDate.now();

        LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        List<DailyCodingTimeDto> daily = IntStream.range(0,7)
                .mapToObj(i -> {
                    LocalDate currentDate = monday.plusDays(i);
                    Long time = codingSessionRepository.sumBetweenCodingTime(userId,currentDate,currentDate);
                    return new DailyCodingTimeDto(currentDate,time);
                }).collect(Collectors.toList());


        // 주별 코딩 시간 합계
        Long sum = daily.stream().mapToLong(DailyCodingTimeDto::getCodingTimeMs).sum();

        // 주별 코딩 시간 평균
        Long avg = sum > 0 ? sum/7 : 0L;

        // 주별 중 가장 코딩을 많이 한 시간
        Long max = daily.stream()
                .map(DailyCodingTimeDto::getCodingTimeMs)
                .max(Long::compareTo)
                .orElse(0L);

        return new WeeklyCodingTimeDto(daily,avg,max,sum);
    }

    // 받는 데이터
    public void saveCodingTime(CodingTimeRequestDto dto, Long userId){
        CodingSession session = new CodingSession(userId,dto.getContainerId(),dto.getCodingTimeMs(),dto.getRecordDate());
        codingSessionRepository.save(session);
    }


}
