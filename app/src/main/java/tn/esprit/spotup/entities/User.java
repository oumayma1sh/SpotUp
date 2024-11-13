package tn.esprit.spotup.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
public class User {

    public static final List<User> userArrayList = new ArrayList<User>();

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "uid")
    private int uid;

    @ColumnInfo(name = "first_name")
    private String firstName;

    @ColumnInfo(name = "last_name")
    private String lastName;

    @ColumnInfo(name = "email", index = true)
    private String email;

    @ColumnInfo(name = "gender")
    private String gender;

    @ColumnInfo(name = "password")
    private String password;

    @ColumnInfo(name = "role")
    private String role;

    @ColumnInfo(name = "birthday")
    private Date birthday;

    @ColumnInfo(name = "phone_number")
    private String phoneNumber;

    @ColumnInfo(name = "about_me")
    private String aboutMe;

    @ColumnInfo(name = "profile_picture")
    private String profilePicture;

    @ColumnInfo(name = "cover_picture")
    private String coverPicture;

    public User() {
    }

    public User(String firstName, String lastName, String email, String gender, String password, String role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.gender = gender;
        this.password = password;
        this.role = role;
    }

    public User(int uid, String firstName, String lastName, String email, String gender, String password, String role, Date birthday, String phoneNumber, String aboutMe, String profilePicture, String coverPicture) {
        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.gender = gender;
        this.password = password;
        this.role = role;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
        this.aboutMe = aboutMe;
        this.profilePicture = profilePicture;
        this.coverPicture = coverPicture;
    }

    public static User getUserForEmail(String passedEmail) {
        for(User user : userArrayList)
        {
            if(Objects.equals(user.getEmail(), passedEmail))
                return user;
        }
        return null;
    }
    public static User getUserForPhone(String passedPhone) {
        for(User user : userArrayList)
        {
            if(Objects.equals(user.getPhoneNumber(), passedPhone))
                return user;
        }
        return null;
    }

    public static User getUserForId(int passedUserId) {
        for(User user : userArrayList)
        {
            if(user.getUid() == passedUserId)
                return user;
        }
        return null;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getCoverPicture() {
        return coverPicture;
    }

    public void setCoverPicture(String coverPicture) {
        this.coverPicture = coverPicture;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid=" + uid +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", gender='" + gender + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                ", birthday=" + birthday +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", aboutMe='" + aboutMe + '\'' +
                ", profilePicture='" + profilePicture + '\'' +
                ", coverPicture='" + coverPicture + '\'' +
                '}';
    }
}
