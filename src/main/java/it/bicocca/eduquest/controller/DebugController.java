package it.bicocca.eduquest.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/api/auth/debug")
public class DebugController {

    private final JdbcTemplate jdbcTemplate;

    public DebugController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping("/expire-missions")
    @Transactional
    public String expireMissions() {
        try {
            jdbcTemplate.update("UPDATE mission_progress SET assignment_date = '2020-01-01'");
        } catch (Exception e) {
            jdbcTemplate.update("UPDATE MISSION_PROGRESS SET ASSIGNMENT_DATE = '2020-01-01'");
        }
        
        return "Done!";
    }
    
    @PostMapping("/expire-challenges")
    @Transactional
    public String expireChallenges() {
        try {
            jdbcTemplate.update("UPDATE challenges SET expires_at = '2020-01-01 00:00:00' WHERE status = 'ACTIVE'");
        } catch (Exception e) {
            jdbcTemplate.update("UPDATE CHALLENGES SET EXPIRES_AT = '2020-01-01 00:00:00' WHERE STATUS = 'ACTIVE'");
        }
        
        return "Challenges expiration dates forced to past!";
    }
}