package com.agutsul.chess.timeout;

import java.time.Duration;

public interface IncrementalTimeout<TIMEOUT extends BaseTimeout> {
    TIMEOUT getTimeout();
    Duration getExtraDuration();
}