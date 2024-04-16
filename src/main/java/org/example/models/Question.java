package org.example.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class Question {
    private  int id ;
    private String question;
    private ArrayList<String> reponse ;
    private  LocalDateTime date ;

    public Question(int id, String question, LocalDateTime date, Boolean active) {
        this.id = id;
        this.question = question;
        this.date = date;
        this.active = active;
    }

    public Question(int id, String question, ArrayList<String> reponse, LocalDateTime date, Boolean active) {
        this.id = id;
        this.question = question;
        this.reponse = reponse;
        this.date = date;
        this.active = active;
    }

    private Boolean active ;

    public Question(int id, String question) {
        this.id = id;
        this.question = question;
    }

    public Question( String question) {
        this.question = question;
    }

    public Question() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public ArrayList<String> getReponse() {
        return reponse;
    }

    public void setReponse(ArrayList<String> reponse) {
        this.reponse = reponse;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Question)) return false;
        Question question1 = (Question) o;
        return getId() == question1.getId() && Objects.equals(getQuestion(), question1.getQuestion()) && Objects.equals(getReponse(), question1.getReponse());
    }


}
