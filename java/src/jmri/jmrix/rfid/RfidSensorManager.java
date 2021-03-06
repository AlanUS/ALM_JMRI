package jmri.jmrix.rfid;

import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manage the Rfid-specific Sensor implementation.
 * <p>
 * System names are "FSpppp", where ppp is a representation of the RFID reader.
 *
 * @author Bob Jacobsen Copyright (C) 2007
 * @author Matthew Harris Copyright (C) 2011
 * @since 2.11.4
 */
abstract public class RfidSensorManager extends jmri.managers.AbstractSensorManager implements RfidListener {

    public RfidSensorManager(RfidSystemConnectionMemo memo) {
        super(memo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public RfidSystemConnectionMemo getMemo() {
        return (RfidSystemConnectionMemo) memo;
    }

    // to free resources when no longer used
    @Override
    public void dispose() {
        super.dispose();
    }

//    /**
//     * {@inheritDoc}
//     */
//        @Nonnull
//        public Sensor createNewSensor(@Nonnull String systemName, String userName) {
//        RfidSensor r = new RfidSensor(systemName, userName);
//        return r;
//    }

    @Override
    public void message(RfidMessage m) {
        log.warn("Unexpected message received: {}", m);
    }

    private static final Logger log = LoggerFactory.getLogger(RfidSensorManager.class);

}
