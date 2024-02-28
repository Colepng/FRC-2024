package ca.warp7.frc2024.subsystems.feeder;

import ca.warp7.frc2024.Constants;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import org.littletonrobotics.junction.Logger;

public class FeederSubsystem extends SubsystemBase {
    private final FeederIO io;
    private final FeederIOInputsAutoLogged inputs = new FeederIOInputsAutoLogged();
    private final SimpleMotorFeedforward feedforward;
    private final PIDController feedback;

    private Double velocity;

    public FeederSubsystem(FeederIO io) {
        this.io = io;

        switch (Constants.CURRENT_MODE) {
            case REAL:
                feedforward = new SimpleMotorFeedforward(0, 0, 0);
                feedback = new PIDController(0.0, 0.0, 0.0);
                break;
            case SIM:
                feedforward = new SimpleMotorFeedforward(1, 0, 0);
                feedback = new PIDController(0.0, 0.0, 0.0);
                break;
            default:
                feedforward = new SimpleMotorFeedforward(0, 0, 0);
                feedback = new PIDController(0.0, 0.0, 0.0);
                break;
        }
    }

    public void setRPM(double RPM) {
        velocity = Units.rotationsPerMinuteToRadiansPerSecond(RPM);
    }

    @Override
    public void periodic() {
        io.updateInputs(inputs);

        Logger.processInputs("Feeder/Feeder", inputs);

        if (velocity != null) {
            double voltage = feedback.calculate(inputs.feederVelocityRadPerSec, velocity);
            io.setFeederVoltage(voltage);
        } else {
            io.setFeederVoltage(0);
        }
    }
}
