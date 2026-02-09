package it.bicocca.eduquest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/api/auth/debug")
public class DebugController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

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
}
