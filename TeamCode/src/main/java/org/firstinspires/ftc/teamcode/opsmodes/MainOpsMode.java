package org.firstinspires.ftc.teamcode.opsmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.objects.Robot;
import org.firstinspires.ftc.teamcode.objects.RobotSettings;
import org.firstinspires.ftc.teamcode.resources.HardwareController;

@TeleOp(name="Main OpMode")
public class MainOpsMode extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime();

    // Motors
    private DcMotor leftFrontDrive = null;
    private DcMotor leftBackDrive = null;
    private DcMotor rightFrontDrive = null;
    private DcMotor rightBackDrive = null;

    private DcMotor intakeDrive = null;
    private enum ArmState {
        UP,
        DOWN
    }

    public enum IntakeState {
        IN,
        OUT
    }

    private boolean trapdoorOpened = false;
    private boolean bay1 = false;
    private boolean bay2 = false;
    private ArmState armState = ArmState.DOWN;


    // Constants
    private static final double DRIVE_SPEED = 0.6;
    private static final double INTAKE_SPEED = 0.1;
    // Servo Constants
    // TODO: Get real values for these
    private static final double TRAPDOOR_OPEN = 0;
    private static final double TRAPDOOR_CLOSED = 0;
    private static final double ARM_UP = 0;
    private static final double ARM_DOWN = 0;

    private HardwareController hardwareController;
    private Robot robot;
    //
    private double intakePower = 0;
    private int inState = -1;
    private IntakeState intakeState = IntakeState.IN;

    @Override
    public void runOpMode() throws InterruptedException {
        robot = new Robot(AutonomousOpsMode.Alliance.BLUE_ALLIANCE, AutonomousOpsMode.StartPos.BACKSTAGE, hardwareMap);
        hardwareController = new HardwareController(hardwareMap, robot);
        // Init hardware Vars
        leftFrontDrive = hardwareMap.get(DcMotor.class, RobotSettings.BANA_LFDRIVE_MOTOR);
        leftBackDrive = hardwareMap.get(DcMotor.class, RobotSettings.BANA_LBDRIVE_MOTOR);
        rightFrontDrive = hardwareMap.get(DcMotor.class, RobotSettings.BANA_RFDRIVE_MOTOR);
        rightBackDrive = hardwareMap.get(DcMotor.class, RobotSettings.BANA_RBDRIVE_MOTOR);

        intakeDrive = hardwareMap.get(DcMotor.class, "intake");

        // Set directions
        leftFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        leftBackDrive.setDirection(DcMotor.Direction.REVERSE);
        rightFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        rightBackDrive.setDirection(DcMotor.Direction.FORWARD);

        intakeDrive.setDirection(DcMotorSimple.Direction.FORWARD);

        // Wait for the game to start (driver presses PLAY)
        telemetry.addData("Status", "Initialized");
        telemetry.update();
        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            updateDriveMotors();
            updateServos();

            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.update();
        }
    }

    private float armX = 0.5f; // this is at the bottom
    private void updateServos(){
        double gamePadY = gamepad2.left_stick_y;

        armX += gamePadY / 1000.0f;
        if(armX > 1.0f) armX = 1.0f;
        if(armX < 0.0f) armX = 0.0f;
        hardwareController.servoMove(armX, HardwareController.Servo_Type.ARM_SERVO);
        telemetry.addData("Arm Pos", "%f", armX);

        // Check for bucket
        if (gamepad2.y) {
            hardwareController.servoMove(0.0f, HardwareController.Servo_Type.BUCKET_SERVO);
        }
        if(gamepad2.b){
            hardwareController.servoMove(1.0f, HardwareController.Servo_Type.BUCKET_SERVO);
        }
        if(gamepad2.x){
            hardwareController.servoMove(0.0f, HardwareController.Servo_Type.DOOR_SERVO);
        }
        if(gamepad2.a){
            hardwareController.servoMove(1.0f, HardwareController.Servo_Type.DOOR_SERVO);
        }

        telemetry.update();
    }

    private void updateDriveMotors() {
        double max;

        // POV Mode uses left joystick to go forward & strafe, and right joystick to rotate.
        double axial = -gamepad1.left_stick_y;
        double lateral = gamepad1.left_stick_x;
        double yaw = gamepad1.right_stick_x;

        //fine control using dpad and bumpers
        if (gamepad1.dpad_up)
            axial += 0.3;
        if (gamepad1.dpad_down)
            axial -= 0.3;
        if (gamepad1.dpad_left)
            lateral -= 0.3;
        if (gamepad1.dpad_right)
            lateral += 0.3;
        if (gamepad1.left_bumper)
            yaw -= 0.3 * -1;
        if (gamepad1.right_bumper)
            yaw += 0.3 * -1;

        // Combine the joystick requests for each axis-motion to determine each wheel's power.
        // Set up a variable for each drive wheel to save the power level for telemetry.
        double leftFrontPower = axial + lateral + yaw;
        double rightFrontPower = axial - lateral - yaw;
        double leftBackPower = axial - lateral + yaw;
        double rightBackPower = axial + lateral - yaw;

        leftFrontPower *= -DRIVE_SPEED;
        rightFrontPower *= DRIVE_SPEED;
        leftBackPower *= DRIVE_SPEED;
        rightBackPower *= DRIVE_SPEED;

        // Normalize the values so no wheel power exceeds 100%
        // This ensures that the robot maintains the desired motion.
        max = Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower));
        max = Math.max(max, Math.abs(leftBackPower));
        max = Math.max(max, Math.abs(rightBackPower));

        if (max > 1.0) {
            leftFrontPower /= max;
            rightFrontPower /= max;
            leftBackPower /= max;
            rightBackPower /= max;
        }

        // Send calculated power to wheels
        leftFrontDrive.setPower(leftFrontPower);
        rightFrontDrive.setPower(rightFrontPower);
        leftBackDrive.setPower(leftBackPower);
        rightBackDrive.setPower(rightBackPower);

        // Intake
        intakePower = 0;

        if(gamepad1.right_trigger != 0) {
            intakePower = 1;
        }

        if(gamepad1.left_trigger != 0) {
            intakePower = -1;
        }

        intakeDrive.setPower(intakePower);

    }
}
