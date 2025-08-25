package com.tritonptms.public_transport_management_system.utils;

import com.tritonptms.public_transport_management_system.dto.LogDetail;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.locationtech.jts.geom.LineString;

public class ObjectComparisonUtil {

    public static <T> List<LogDetail> compareObjects(T oldObject, T newObject) {
        List<LogDetail> changes = new ArrayList<>();
        if (oldObject == null || newObject == null) {
            return changes;
        }

        Field[] fields = oldObject.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object oldValue = field.get(oldObject);
                Object newValue = field.get(newObject);

                // Skip ID field as it should not be logged
                if (field.getName().equals("id")) {
                    continue;
                }

                // Special handling for List types like majorStops
                if (field.getType().equals(List.class)) {
                    List<?> oldList = (List<?>) oldValue;
                    List<?> newList = (List<?>) newValue;
                    if (!Objects.equals(oldList, newList)) {
                        changes.add(new LogDetail(
                                field.getName(),
                                oldList != null ? oldList.toString() : "null",
                                newList != null ? newList.toString() : "null"));
                    }
                    continue;
                }

                // Special handling for LineString types
                if (field.getType().equals(LineString.class)) {
                    LineString oldPath = (LineString) oldValue;
                    LineString newPath = (LineString) newValue;
                    if (!Objects.equals(oldPath, newPath)) {
                        changes.add(new LogDetail(
                                field.getName(),
                                oldPath != null ? "Path Data" : "null",
                                newPath != null ? "Path Data" : "null"));
                    }
                    continue;
                }

                // Default comparison for other fields
                if (!Objects.equals(oldValue, newValue)) {
                    changes.add(new LogDetail(
                            field.getName(),
                            String.valueOf(oldValue),
                            String.valueOf(newValue)));
                }
            } catch (IllegalAccessException e) {
                System.err.println("Error accessing field: " + field.getName());
            }
        }
        return changes;
    }
}