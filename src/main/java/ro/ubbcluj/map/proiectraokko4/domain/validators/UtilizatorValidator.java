package ro.ubbcluj.map.proiectraokko4.domain.validators;



import ro.ubbcluj.map.proiectraokko4.domain.Utilizator;

import java.util.regex.Pattern;

/**
 * user validator class
 */
public class UtilizatorValidator implements Validator<Utilizator> {
    @Override
    public void validate(Utilizator entity) throws ValidationException {
        String onlyCharacters = "^[a-zA-Z]*$";
        if (entity == null)
            throw new ValidationException("User most not be null");
        if (entity.getId().getClass().getName() != "java.lang.Long")
            throw new ValidationException("Id must be a number");
        if (entity.getFirstName().getClass().getName() != "java.lang.String")
            throw new ValidationException("First name must be a string");
        if (entity.getLastName().getClass().getName() != "java.lang.String")
            throw new ValidationException("Last name must be a string");
        if (!Pattern.matches(onlyCharacters, entity.getFirstName()))
            throw new ValidationException("First name must contain only characters");
        if (!Pattern.matches(onlyCharacters, entity.getLastName()))
            throw new ValidationException("Last name must contain only characters");
        if(entity.getFirstName().equals(""))
            throw new ValidationException("nume vid");
        if(entity.getLastName().equals(""))
            throw new ValidationException("nume vid");
        if(entity.getUsername().equals(""))
            throw new ValidationException("username-ul nu poate fi vid");
    }
}
