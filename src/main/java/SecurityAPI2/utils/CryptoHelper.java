package SecurityAPI2.utils;

import SecurityAPI2.Crypto.SymetricKeyDecription;
import SecurityAPI2.Crypto.SymetricKeyEncription;
import SecurityAPI2.Model.Address;
import SecurityAPI2.Model.Engineer;
import SecurityAPI2.Model.ProjectEmployee;
import SecurityAPI2.Model.User;
import io.github.cdimascio.dotenv.Dotenv;

import javax.xml.bind.DatatypeConverter;
import java.util.List;

public class CryptoHelper {
    private static final String nameKey = Dotenv.load().get("NAME_KEY");
    private static final String surnameKey = Dotenv.load().get("SURNAME_KEY");
    private static final String addressKey = Dotenv.load().get("ADDRESS_KEY");
    private static final String phoneKey = Dotenv.load().get("PHONE_KEY");
    private static final String emailKey = Dotenv.load().get("EMAIL_KEY");
    private static final String authSecretKey = Dotenv.load().get("AUTH_SECRET_KEY");


    public static User decryptUser(User user) {
        String name  = SymetricKeyDecription.decrypt(user.getName(), DatatypeConverter.parseHexBinary(nameKey));
        String surname = SymetricKeyDecription.decrypt(user.getSurname(), DatatypeConverter.parseHexBinary(surnameKey));
        String phoneNumber = SymetricKeyDecription.decrypt(user.getPhoneNumber(), DatatypeConverter.parseHexBinary(phoneKey));
        Address address = decryptAddress(user.getAddress());
        String authSecret = SymetricKeyDecription.decrypt(user.getAuthSecret(), DatatypeConverter.parseHexBinary(authSecretKey));
        user.setName(name);
        user.setSurname(surname);
        user.setPhoneNumber(phoneNumber);
        user.setAddress(address);
        user.setAuthSecret(authSecret);
        return user;
    }

    public static User encryptUser(User user){
        String name = SymetricKeyEncription.encrypt(user.getName(), DatatypeConverter.parseHexBinary(nameKey));
        String surname = SymetricKeyEncription.encrypt(user.getSurname(), DatatypeConverter.parseHexBinary(surnameKey));
        String phoneNumber = SymetricKeyEncription.encrypt(user.getPhoneNumber(), DatatypeConverter.parseHexBinary(phoneKey));
//        String email = SymetricKeyEncription.encrypt(user.getEmail(), DatatypeConverter.parseHexBinary(emailKey));
        Address address = encryptAddress(user.getAddress());
        String authSecret = SymetricKeyEncription.encrypt(user.getAuthSecret(), DatatypeConverter.parseHexBinary(authSecretKey));
        user.setName(name);
        user.setSurname(surname);
        user.setPhoneNumber(phoneNumber);
//        user.setEmail(email);
        user.setAddress(address);
        user.setAuthSecret(authSecret);
        return user;
    }

    private static Address encryptAddress(Address address) {
        String street = SymetricKeyEncription.encrypt(address.getStreet(), DatatypeConverter.parseHexBinary(addressKey));
        String streetNumber = SymetricKeyEncription.encrypt(address.getStreetNumber(), DatatypeConverter.parseHexBinary(addressKey));
        String city = SymetricKeyEncription.encrypt(address.getCity(),  DatatypeConverter.parseHexBinary(addressKey));
        String zipCode = SymetricKeyEncription.encrypt(address.getZipCode(),  DatatypeConverter.parseHexBinary(addressKey));
        String country = SymetricKeyEncription.encrypt(address.getCountry(),  DatatypeConverter.parseHexBinary(addressKey));
        address.setStreet(street);
        address.setStreetNumber(streetNumber);
        address.setCity(city);
        address.setZipCode(zipCode);
        address.setCountry(country);
        return address;
    }

    private static Address decryptAddress(Address address){
        String street = SymetricKeyDecription.decrypt(address.getStreet(), DatatypeConverter.parseHexBinary(addressKey));
        String streetNumber = SymetricKeyDecription.decrypt(address.getStreetNumber(), DatatypeConverter.parseHexBinary(addressKey));
        String city = SymetricKeyDecription.decrypt(address.getCity(),  DatatypeConverter.parseHexBinary(addressKey));
        String zipCode = SymetricKeyDecription.decrypt(address.getZipCode(),  DatatypeConverter.parseHexBinary(addressKey));
        String country = SymetricKeyDecription.decrypt(address.getCountry(),  DatatypeConverter.parseHexBinary(addressKey));
        address.setStreet(street);
        address.setStreetNumber(streetNumber);
        address.setCity(city);
        address.setZipCode(zipCode);
        address.setCountry(country);
        return address;
    }

    public static List<User> decryptUsers(List<User> users) {
        for(User user : users){
            decryptUser(user);
        }
        return users;
    }

    public static List<ProjectEmployee> decryptProjectEmployees(List<ProjectEmployee> employees){
        for (ProjectEmployee employee : employees){
            employee.setEmployee(decryptUser(employee.getEmployee()));
        }
        return employees;
    }

    public static List<Engineer> decryptEngineers(List<Engineer> employees){
        for (Engineer employee : employees){
            employee.setUser(decryptUser(employee.getUser()));
        }
        return employees;
    }

    public static Engineer decryptEngineer(Engineer engineer) {
        engineer.setUser(decryptUser(engineer.getUser()));
        return engineer;
    }
}
