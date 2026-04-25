package com.example.demo.repository;

import com.example.demo.entity.ScanEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScanEventRepository extends JpaRepository<ScanEvent, Long> {

    long countByToken(String token);

    @Query(value = "SELECT DATE(scanned_at) AS date, COUNT(*) AS count " +
            "FROM scan_events WHERE token = :token " +
            "GROUP BY DATE(scanned_at) ORDER BY DATE(scanned_at)",
            nativeQuery = true)
    List<Object[]> countByDay(@Param("token") String token);
}
