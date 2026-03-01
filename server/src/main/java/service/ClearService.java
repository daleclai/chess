package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;

public class ClearService {
    private final DataAccess dataAccess;
    public ClearService(DataAccess dataAccess, DataAccess dataAccess1) {
        this.dataAccess = dataAccess;
    }
    public void clear() throws DataAccessException {
        dataAccess.clear();
    }

}
