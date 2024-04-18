package org.example.models;

import java.util.Objects;

public class Formulaireq {
    private int id;
    private int idq;

    public Formulaireq(int id, int idq) {
        this.id = id;
        this.idq = idq;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdq() {
        return idq;
    }

    public void setIdq(int idq) {
        this.idq = idq;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Formulaireq)) return false;
        Formulaireq that = (Formulaireq) o;
        return getId() == that.getId() && getIdq() == that.getIdq();
    }}


