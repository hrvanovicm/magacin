package fyi.hrvanovicm.magacin.shared.dto;

import fyi.hrvanovicm.magacin.shared.embeddable.Audit;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class AuditDTO {
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AuditDTO fromEntity(Audit audit) {
        var dto = new AuditDTO();
        dto.setCreatedAt(audit.getCreatedAt());
        dto.setUpdatedAt(audit.getUpdatedAt());
        return dto;
    }

    @SuppressWarnings("DuplicatedCode")
    public Audit toEntity() {
        var audit = new Audit();
        audit.setCreatedAt(createdAt);
        audit.setUpdatedAt(updatedAt);
        return audit;
    }
}
