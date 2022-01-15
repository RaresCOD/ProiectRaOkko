package ro.ubbcluj.map.proiectraokko4.domain.validators;


import ro.ubbcluj.map.proiectraokko4.domain.Message;


public class MessageValidator implements Validator<Message>{
    @Override
    public void validate(Message entity) throws ValidationException {
        if(entity.getMsg() != null && entity.getMsg().equals("")) throw  new ValidationException("Mesajul nu poate fi gol");
    }
}
