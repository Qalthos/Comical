/*
 * Comical: An Android webcomic manager
 * Copyright (C) 2017  Nathaniel Case
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.linkybook.comical.utils;

import java.time.DayOfWeek;
import java.util.ArrayList;

public class Schedule {
    public static ArrayList<DayOfWeek> decodeUpdates(int updates) {
        ArrayList<DayOfWeek> days = new ArrayList<>();
        for(DayOfWeek dayOfWeek : DayOfWeek.values()) {
            if((updates >> dayOfWeek.getValue() & 1) == 1) {
                days.add(dayOfWeek);
            }
        }
        return days;
    }

    public static int encodeUpdates(ArrayList<DayOfWeek> days) {
        int updates = 0;
        for(DayOfWeek dayOfWeek: days) {
            updates += 1 << dayOfWeek.getValue();
        }
        return updates;
    }
}
