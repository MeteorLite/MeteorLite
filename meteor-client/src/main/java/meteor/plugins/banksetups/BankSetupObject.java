package meteor.plugins.banksetups;

import java.util.List;

public class BankSetupObject {
    String name;
    List<int[]> inventory;
    List<int[]> equipment;
    public BankSetupObject(String n, List<int[]> i, List<int[]> e){
        name =n;
        inventory=i;
        equipment=e;
    }
}
