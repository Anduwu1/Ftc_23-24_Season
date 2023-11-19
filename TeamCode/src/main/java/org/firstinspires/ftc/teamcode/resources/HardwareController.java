package org.firstinspires.ftc.teamcode.resources;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.objects.Robot;
import org.firstinspires.ftc.teamcode.roadrunner.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.subsystems.Arm;
import org.firstinspires.ftc.teamcode.subsystems.Bucket;
import org.firstinspires.ftc.teamcode.subsystems.DistanceSensors;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
/*
    Class for making the robot like
    do stuff
 */
public class HardwareController{

    public enum Servo_Type{
        ARM_SERVO,
        BUCKET_SERVO,
        DOOR_SERVO
    }

    // Subsystems
    private Bucket bucket;
    private Arm arm;
    private Intake intake;
    private DistanceSensors dsensors;

    // Robot we're controlling
    public Robot robot;

    // Roadrunner drive
    SampleMecanumDrive drive;
    HardwareMap hardwareMap;

    // Pass in a hardware map please
    @SuppressLint("SuspiciousIndentation")
    public HardwareController(HardwareMap _hardwareMap){
        this.hardwareMap = _hardwareMap;

        bucket = new Bucket();
        arm = new Arm();
        intake = new Intake();
        dsensors = new DistanceSensors();

        bucket.bucketRotation = this.hardwareMap.get(Servo.class, bucket.ROTATION_SERVO);
        bucket.bucketTrapdoor = this.hardwareMap.get(Servo.class, bucket.TRAPDOOR_SERVO);

        arm.armServo = this.hardwareMap.get(Servo.class, arm.arm);

        dsensors.left = this.hardwareMap.get(DistanceSensor.class, dsensors.leftSense);
        dsensors.right = this.hardwareMap.get(DistanceSensor.class, dsensors.rightSense);


        try {
            drive = new SampleMecanumDrive(this.hardwareMap);
        }catch (Exception e){

        }
    }

    /*
        DRIVE STUFF
     */
    public void driveTo(float xPos, float yPos){
        drive.followTrajectory(drive.trajectoryBuilder(new Pose2d())
                .splineTo(new Vector2d(xPos, yPos), Math.toRadians(0))
                .build());
    }


    public void servoMove(float to, @NonNull Servo_Type sT){
        switch (sT){
            case ARM_SERVO:
                arm.armServo.setPosition(to);
                break;
            case DOOR_SERVO:
                bucket.bucketTrapdoor.setPosition(to);
                break;
            case BUCKET_SERVO:
                bucket.bucketRotation.setPosition(to);
                break;
        }
    }

    public void align() {
        // TODO: Add alignment code
        // Example distnace sensor distance stuff
        // You have to choose what units you want, I would suggest inches because that's what everything uses
        dsensors.left.getDistance(DistanceUnit.INCH);
        dsensors.right.getDistance(DistanceUnit.INCH);
    }

}
