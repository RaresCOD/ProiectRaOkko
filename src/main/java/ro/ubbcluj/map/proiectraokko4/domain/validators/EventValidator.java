package ro.ubbcluj.map.proiectraokko4.domain.validators;

import ro.ubbcluj.map.proiectraokko4.domain.Event;

public class EventValidator implements Validator<Event> {

    @Override
    public void validate(Event entity) throws ValidationException {
        if(entity.getName().equals(""))
            throw new ValidationException("Numele evenimentului nu poate fi gol.");
        if(entity.getDescription().equals(""))
            throw new ValidationException("Descrierea evenimentului nu poate fi goala.");
        if(entity.getLocation().equals(""))
            throw new ValidationException("Locatia evenimentului nu poate lipsi.");
        if(entity.getName().length() > 25)
            throw new ValidationException("Numele evenimentului este prea lung (maxim 25 de caractere).");
        if(entity.getLocation().length() > 30)
            throw new ValidationException("Numele locatiei este prea lung (maxim 30 de caractere).");
        if(entity.getDescription().length() > 140)
            throw new ValidationException("Descrierea evenimentului este prea lunga (maxim 140 de caractere).");
    }
}
