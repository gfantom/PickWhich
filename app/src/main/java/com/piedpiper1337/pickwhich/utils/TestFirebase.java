package com.piedpiper1337.pickwhich.utils;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * Created by Victor Kwak on 2/1/16.
 */
public class TestFirebase {

    public static class User {
        private String username;
        private String phoneNumber;

        public User(String phoneNumber, String username) {
            this.phoneNumber = phoneNumber;
            this.username = username;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }
    }
    public static void main(String[] args) {
        User victorkwak = new User("4242584137", "victorkwak");

        Firebase firebaseRef = new Firebase(Constants.BackEnd.URL);
        Firebase victorRef = firebaseRef.child(Constants.BackEnd.USERS).child(victorkwak.getUsername());
        victorRef.setValue(victorkwak);

        firebaseRef.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println(dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
}
