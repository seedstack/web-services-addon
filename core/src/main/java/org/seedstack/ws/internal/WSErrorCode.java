/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.ws.internal;

import org.seedstack.shed.exception.ErrorCode;

enum WSErrorCode implements ErrorCode {
    ENDPOINT_URL_MISSING,
    MALFORMED_ENDPOINT_URL,
    UNABLE_TO_FIND_WSDL,
    WSDL_LOCATION_MISSING
}
