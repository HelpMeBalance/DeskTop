package org.example.utils;

import javafx.util.StringConverter;
import org.example.models.RendezVous;
import org.example.models.User;

public class AppointmentStringConverter extends StringConverter<RendezVous> {
    @Override
    public String toString(RendezVous rv) {
        if (rv != null) {
            return rv.getPatient().getFirstname() + " " + rv.getPatient().getLastname() + ": "+ rv.getNomService();
        }
        return null;
    }

    @Override
    public RendezVous fromString(String s) {
        return null;
    }
}
