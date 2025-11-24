package com.example.aura.Event.Certification;

import com.example.aura.Event.Base.BaseEvent;
import lombok.Getter;

@Getter
public class CertificationValidatedEvent extends BaseEvent {

    private final Long certificationId;
    private final Long technicianId;
    private final String technicianEmail;
    private final String certificationTitle;
    private final String adminName;

    public CertificationValidatedEvent(Object source, Long certificationId,
                                       Long technicianId, String technicianEmail,
                                       String certificationTitle, String adminName) {
        super(source, "CERTIFICATION_VALIDATED");
        this.certificationId = certificationId;
        this.technicianId = technicianId;
        this.technicianEmail = technicianEmail;
        this.certificationTitle = certificationTitle;
        this.adminName = adminName;
    }
}