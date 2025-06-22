package service;


import model.User;

public interface IService extends IObservable {
    User login(String username, String password, IObserver clientObserver) throws ServiceException;
    boolean logout(String username, IObserver clientObserver) throws ServiceException;
}
