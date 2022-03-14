// IMyAidlInterface.aidl
package com.example.workdemo2;
//添加自定义的parcelable对象
parcelable People;

// Declare any non-default types here with import statements

interface IMyAidlInterface {

    String getName();
    People getPeople();
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);
}