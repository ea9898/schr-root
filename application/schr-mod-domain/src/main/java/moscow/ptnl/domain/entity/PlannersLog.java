package moscow.ptnl.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "PLANNERS_LOG")
@SequenceGenerator(name = "SEQ_PLANNERS_LOG_ID", sequenceName = "SEQ_PLANNERS_LOG_ID", allocationSize = 1)
public class PlannersLog extends BaseEntity<Long> implements Serializable {

    private static final long serialVersionUID = 975019760398908577L;

    @Id
    @Column(name = "ID", unique = true, nullable = false)
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="SEQ_PLANNERS_LOG_ID")
    private Long id;

    @Size(max = 1000)
    @Column(name = "PLANNER", nullable = false)
    private String planner;

    @Size(max = 1000)
    @Column(name = "HOST", nullable = false)
    private String host;

    @Size(max = 4000)
    @Column(name = "ERROR")
    private String error;

    @Column(name = "DATE_START", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "DATE_END")
    private LocalDateTime endDate;

    @Column(name = "DATE_MONITORING")
    private LocalDateTime dateMonitoring;

    public PlannersLog() {
    }

    public PlannersLog(String planner, String host) {
        this.planner = planner;
        this.host = host;
        this.startDate = LocalDateTime.now();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlanner() {
        return planner;
    }

    public void setPlanner(String planner) {
        this.planner = planner;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public LocalDateTime getDateMonitoring() {
        return dateMonitoring;
    }

    public void setDateMonitoring(LocalDateTime dateMonitoring) {
        this.dateMonitoring = dateMonitoring;
    }
}
