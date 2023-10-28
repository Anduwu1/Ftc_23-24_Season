package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.Servo;

public class Bucket {
    public static final String ROTATION_SERVO = "servoR";
    public static final String TRAPDOOR_SERVO = "servoT";

    public static final float DROP_POS = 0.89f;
    public static final float INTAKE_POS = .19f;

    public Servo bucketRotation = null;
    public Servo bucketTrapdoor = null;
}
