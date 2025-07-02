package com.agutsul.chess.timeout;

import java.time.Duration;

public interface IncrementalTimeout {
    Timeout  getTimeout();
    Duration getExtraDuration();
}