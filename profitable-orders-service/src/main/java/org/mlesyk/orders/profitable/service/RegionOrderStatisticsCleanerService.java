package org.mlesyk.orders.profitable.service;

import lombok.extern.slf4j.Slf4j;
import org.mlesyk.orders.profitable.model.RegionOrderStatistics;
import org.mlesyk.orders.profitable.repository.RegionOrderStatisticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RegionOrderStatisticsCleanerService {

    private final RegionOrderStatisticsRepository regionOrderStatisticsRepository;

    @Autowired
    public RegionOrderStatisticsCleanerService(RegionOrderStatisticsRepository regionOrderStatisticsRepository) {
        this.regionOrderStatisticsRepository = regionOrderStatisticsRepository;
    }

    @Value("${app.orders.statistics_store_days_limit}")
    Integer storeDaysLimit;

    @Value("${app.services.statistics_enabled}")
    boolean statisticsEnabled;

    @Scheduled(cron = "0 * * * * *")
    public void cleanerScheduler() {
        if (statisticsEnabled) {
            Date dateLimit = java.sql.Timestamp.valueOf(LocalDateTime.now().minusDays(storeDaysLimit));

            List<Integer> typeIds = regionOrderStatisticsRepository.findDistinctTypesId();
            for (Integer typeId : typeIds) {
                List<RegionOrderStatistics> statistics = regionOrderStatisticsRepository.findAllByTypeId(typeId);
                statistics = statistics.stream().filter(e -> e.getDate().before(dateLimit)).toList();
                if (!statistics.isEmpty()) {
                    log.info("Deleting statistics for typeId:{} deleteCount:{}", typeId, statistics.size());
                    regionOrderStatisticsRepository.deleteAll(statistics);
                }
            }
        }
    }
}
