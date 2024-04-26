package org.example.models;

import java.time.LocalDateTime;

public class RendezVous {
    private int id;
    private LocalDateTime dateR;
    private String nomService;
    private boolean statut,certificat;
    private User psy,patient;

    public RendezVous(int id, LocalDateTime dateR, String nomService, boolean statut, boolean certificat, User psy, User patient) {
        this.id = id;
        this.dateR = dateR;
        this.nomService = nomService;
        this.statut = statut;
        this.certificat = certificat;
        this.psy = psy;
        this.patient = patient;
    }

    public RendezVous(LocalDateTime dateR, String nomService, boolean statut, boolean certificat, User psy, User patient) {
        this.dateR = dateR;
        this.nomService = nomService;
        this.statut = statut;
        this.certificat = certificat;
        this.psy = psy;
        this.patient = patient;
    }

    public RendezVous() {

    }

    public void update(LocalDateTime dateR, String nomService, boolean statut, boolean certificat, User psy, User patient) {
        this.dateR = dateR;
        this.nomService = nomService;
        this.statut = statut;
        this.certificat = certificat;
        this.psy = psy;
        this.patient = patient;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDateR() {
        return dateR;
    }

    public void setDateR(LocalDateTime dateR) {
        this.dateR = dateR;
    }

    public String getNomService() {
        return nomService;
    }

    public void setNomService(String nomService) {
        this.nomService = nomService;
    }

    public boolean isStatut() {
        return statut;
    }

    public void setStatut(boolean statut) {
        this.statut = statut;
    }

    public boolean isCertificat() {
        return certificat;
    }

    public void setCertificat(boolean certificat) {
        this.certificat = certificat;
    }

    public User getPsy() {
        return psy;
    }

    public void setPsy(User psy) {
        this.psy = psy;
    }

    public User getPatient() {
        return patient;
    }

    public void setPatient(User patient) {
        this.patient = patient;
    }

    @Override
    public String toString() {
        return "RendezVous{" +
                "id=" + id +
                ", dateR=" + dateR +
                ", nomService='" + nomService + '\'' +
                ", statut=" + statut +
                ", certificat=" + certificat +
                ", psy=" + psy +
                ", patient=" + patient +
                '}';
    }
}
