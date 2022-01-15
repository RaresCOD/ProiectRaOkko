package ro.ubbcluj.map.proiectraokko4.domain.validators;


import ro.ubbcluj.map.proiectraokko4.domain.Friendship;

/**
 * FriendshipValidator
 */
public class FriendshipValidator implements Validator<Friendship>{

    @Override
    public void validate(Friendship entity) throws ValidationException {
        if(entity.getId().getLeft() < 0 || entity.getId().getRight() < 0) throw new ValidationException("Id-urile user-ilor nu pot fi negative");
    }
}
