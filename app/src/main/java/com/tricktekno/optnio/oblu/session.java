package com.tricktekno.optnio.oblu;

import java.io.Serializable;
import java.util.List;

public class session implements Serializable {
    Double distance;
    int heading;
    String logFileName;
    int step_count;
    String[] tag_data = new String[200];
    String[] tag_data_image = new String[200];
    List<Double> tag_data_val;
    List<Double> tag_x_val;
    List<Double> tag_y_val;
    Double theta;
    Double x;
    List<Float> x_val_list;
    Double y;
    List<Float> y_val_list;
    Double z;

    public List<Float> get_x() {
        return this.x_val_list;
    }

    public List<Float> get_y() {
        return this.y_val_list;
    }

    public String[] getTag_data() {
        return this.tag_data;
    }

    public String[] getTag_data_image() {
        return this.tag_data_image;
    }

    public List<Double> getTag_data_val() {
        return this.tag_data_val;
    }

    public List<Double> getTag_x_val() {
        return this.tag_x_val;
    }

    public List<Double> getTag_y_val() {
        return this.tag_y_val;
    }

    public int get_heading() {
        return this.heading;
    }

    public int get_step_count() {
        return this.step_count;
    }

    public Double get_distance() {
        return this.distance;
    }

    public Double get_last_x() {
        return this.x;
    }

    public Double get_last_y() {
        return this.y;
    }

    public Double get_last_z() {
        return this.z;
    }

    public Double get_last_theta() {
        return this.theta;
    }

    public String get_logFileName() {
        return this.logFileName;
    }

    public void set_x(List<Float> x_val_list) {
        this.x_val_list = x_val_list;
    }

    public void set_y(List<Float> y_val_list) {
        this.y_val_list = y_val_list;
    }

    public void set_tag_data(String[] tag_data) {
        this.tag_data = tag_data;
    }

    public void set_tag_data_image(String[] tag_data_image) {
        this.tag_data_image = tag_data_image;
    }

    public void setTag_x_val(List<Double> tag_x_val) {
        this.tag_x_val = tag_x_val;
    }

    public void setTag_y_val(List<Double> tag_y_val) {
        this.tag_y_val = tag_y_val;
    }

    public void set_heading(int heading) {
        this.heading = heading;
    }

    public void set_step_count(int step_count) {
        this.step_count = step_count;
    }

    public void set_distance(Double distance) {
        this.distance = distance;
    }

    public void set_last_x(Double x) {
        this.x = x;
    }

    public void set_last_y(Double y) {
        this.y = y;
    }

    public void set_last_z(Double z) {
        this.z = z;
    }

    public void set_last_theta(Double theta) {
        this.theta = theta;
    }

    public void set_logFileName(String logFileName) {
        this.logFileName = logFileName;
    }
}
