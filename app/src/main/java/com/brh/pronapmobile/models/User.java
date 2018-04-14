package com.brh.pronapmobile.models;

import android.util.Log;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.SQLOperator;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Keitel on 4/11/18.
 */
@Table(database = PronapDatabase.class)
public class User extends BaseModel implements Serializable {

    @Column
    @PrimaryKey
    private int id;

    @Column
    private String email;

    @Column
    private String password;

    // status : 1 -> logged in, 0 -> logged out
    @Column
    private int status;

    public User() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public static User find(int id) {
        return SQLite.select().from(User.class).where(User_Table.id.is(id)).querySingle();
    }

    public static boolean isLoggedIn() {
        List<User> users = SQLite.select().from(User.class).queryList();
        if(users.size() > 0) {
            User user = users.get(0);
            if(user.getStatus() == 1)
                return true;
        }

        return false;
    }

    public static boolean matchesPassword(String hashedPassword) {
        List<User> users = SQLite.select().from(User.class).where(User_Table.password.is(hashedPassword)).queryList();
        if(users.size() > 0) {
            return true;
        }

        return false;
    }

    public static boolean hasCard() {
        return SQLite.select(Method.count()).from(Card.class).hasData();
    }

    public static boolean hasVendorProfile() {
        return SQLite.select(Method.count()).from(Vendor.class).hasData();
    }
}