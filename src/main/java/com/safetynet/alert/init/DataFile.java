package com.safetynet.alert.init;

import lombok.Getter;

@Getter
public class DataFile {

    //This is the URL where the file containing information about persons, fire stations and medical records is stored.
    private final String dataUrl = "https://s3-eu-west-1.amazonaws.com/course.oc-static.com/projects/DA+Java+EN/P5+/data.json";
}
