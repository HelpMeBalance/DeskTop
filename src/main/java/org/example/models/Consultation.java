package org.example.models;

public class Consultation {
    private int id;
    private RendezVous appointment;
    private Double duration, rating;
    private boolean recommandation_suivi;
    private String note;
    private User psy,patient;

    public Consultation(int id, RendezVous appointment, double duration, double rating, boolean recommandation_suivi, String note, User psy, User patient) {
        this.id = id;
        this.appointment = appointment;
        this.duration = duration;
        this.rating = rating;
        this.recommandation_suivi = recommandation_suivi;
        this.note = note;
        this.psy = psy;
        this.patient = patient;
    }

    public Consultation(RendezVous appointment, double duration, double rating, boolean recommandation_suivi, String note, User psy, User patient) {
        this.appointment = appointment;
        this.duration = duration;
        this.rating = rating;
        this.recommandation_suivi = recommandation_suivi;
        this.note = note;
        this.psy = psy;
        this.patient = patient;
    }

    public Consultation(RendezVous appointment, User psy, User patient) {
        this.appointment = appointment;
        this.psy = psy;
        this.patient = patient;
        this.note = "";
    }

    public Consultation() {

    }

    public void update(RendezVous appointment, User psy, User patient, String note, double rating){
        this.appointment = appointment;
        this.psy = psy;
        this.patient = patient;
        this.note = note;
        this.rating = rating;
    }

    public void update(String note, boolean followUp){
        this.note = note;
        this.recommandation_suivi = followUp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public RendezVous getAppointment() {
        return appointment;
    }

    public void setAppointment(RendezVous appointment) {
        this.appointment = appointment;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public boolean isRecommandation_suivi() {
        return recommandation_suivi;
    }

    public void setRecommandation_suivi(boolean recommandation_suivi) {
        this.recommandation_suivi = recommandation_suivi;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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
        return "Consultation{" +
                "id=" + id +
                ", appointment=" + appointment +
                ", duration=" + duration +
                ", rating=" + rating +
                ", recommandation_suivi=" + recommandation_suivi +
                ", note='" + note + '\'' +
                ", psy=" + psy +
                ", patient=" + patient +
                '}';
    }
}