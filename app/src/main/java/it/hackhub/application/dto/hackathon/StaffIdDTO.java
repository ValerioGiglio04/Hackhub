package it.hackhub.application.dto.hackathon;

import jakarta.validation.constraints.NotNull;

public class StaffIdDTO {

  @NotNull(message = "L'ID dello staff è obbligatorio")
  private Long staffId;

  public Long getStaffId() {
    return staffId;
  }

  public void setStaffId(Long staffId) {
    this.staffId = staffId;
  }
}
