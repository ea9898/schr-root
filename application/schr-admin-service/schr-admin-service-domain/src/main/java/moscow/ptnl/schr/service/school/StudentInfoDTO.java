package moscow.ptnl.schr.service.school;

import java.time.LocalDateTime;

public class StudentInfoDTO {

    private long attachId;
    private long organizationId;
    private Long areaId;
    private LocalDateTime studChangeDate;

    public long getAttachId() {
        return attachId;
    }

    public void setAttachId(long attachId) {
        this.attachId = attachId;
    }

    public long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

    public LocalDateTime getStudChangeDate() {
        return studChangeDate;
    }

    public void setStudChangeDate(LocalDateTime studChangeDate) {
        this.studChangeDate = studChangeDate;
    }
}
