package service;

public interface IObservable {
    void addObserver(String identifier, IObserver observer) throws ServiceException;
    void removeObserver(String identifier, IObserver observer) throws ServiceException;
    void notifyObservers(ServiceEvent serviceEvent, Object data) throws ServiceException;
}
