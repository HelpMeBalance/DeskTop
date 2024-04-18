package org.example.models;

import java.util.Objects;

public class Reponse {
    private  int id ;
    private String reponse ;
    private Question $question ;

    public Reponse(int id, String reponse, Question $question) {
        this.id = id;
        this.reponse = reponse;
        this.$question = $question;
    }

    public Reponse() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReponse() {
        return reponse;
    }

    public void setReponse(String reponse) {
        this.reponse = reponse;
    }

    public Question get$question() {
        return $question;
    }

    public void set$question(Question $question) {
        this.$question = $question;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reponse)) return false;
        Reponse reponse1 = (Reponse) o;
        return getId() == reponse1.getId() && Objects.equals(getReponse(), reponse1.getReponse()) && Objects.equals(get$question(), reponse1.get$question());
    }


}
