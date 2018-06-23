package com.ail.core.logging;

import java.util.Date;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceInterface;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;

@ServiceInterface
public class LoggingService {
    
    @ServiceArgument
    public interface LoggingArgument extends Argument {
        Date getDate();

        void setDate(Date date);

        Severity getSeverity();

        void setSeverity(Severity severity);

        String getMessage();

        void setMessage(String message);
        
        Throwable getCause();
        
        void setCause(Throwable cause);
    }
    
    @ServiceCommand(defaultServiceClass=SystemOutLoggerService.class)
    public interface LoggingCommand extends LoggingArgument, Command {
    }
}
