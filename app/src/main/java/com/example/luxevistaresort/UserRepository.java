package com.example.luxevistaresort;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UserRepository {
    private final DatabaseHelper dbHelper;

    public enum RegistrationResult {
        SUCCESS,
        EMAIL_EXISTS,
        FAILURE
    }

    public UserRepository(Context context) {
        dbHelper = new DatabaseHelper(context.getApplicationContext());
    }

    // --- User Management Methods ---

    public RegistrationResult register(String name, String email, String password) {
        if (checkUserExists(email)) {
            return RegistrationResult.EMAIL_EXISTS;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_USER_NAME, name);
        values.put(DatabaseHelper.COLUMN_USER_EMAIL, email);
        values.put(DatabaseHelper.COLUMN_USER_PASSWORD, password);
        values.put(DatabaseHelper.COLUMN_USER_ROLE, "GUEST");
        values.put(DatabaseHelper.COLUMN_USER_PREF_ROOM, "Any");
        values.put(DatabaseHelper.COLUMN_USER_PREF_SERVICE, "Any");
        try {
            long result = db.insertOrThrow(DatabaseHelper.TABLE_USERS, null, values);
            return result != -1 ? RegistrationResult.SUCCESS : RegistrationResult.FAILURE;
        } catch (SQLiteException e) {
            e.printStackTrace();
            return RegistrationResult.FAILURE;
        } finally {
            db.close();
        }
    }

    public boolean checkUserExists(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_USERS, new String[]{DatabaseHelper.COLUMN_USER_ID}, DatabaseHelper.COLUMN_USER_EMAIL + " = ?", new String[]{email}, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count > 0;
    }

    public User loginUser(String email, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_USERS, null, DatabaseHelper.COLUMN_USER_EMAIL + " = ? AND " + DatabaseHelper.COLUMN_USER_PASSWORD + " = ?", new String[]{email, password}, null, null, null);
        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = new User(
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_PASSWORD)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ROLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_PREF_ROOM)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_PREF_SERVICE))
            );
        }
        if (cursor != null) cursor.close();
        db.close();
        return user;
    }

    public User getUserByEmail(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_USERS, null, DatabaseHelper.COLUMN_USER_EMAIL + " = ?", new String[]{email}, null, null, null);
        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = new User(
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_PASSWORD)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ROLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_PREF_ROOM)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_PREF_SERVICE))
            );
        }
        if (cursor != null) cursor.close();
        db.close();
        return user;
    }

    public boolean updateUserProfile(String originalEmail, String newName, String newEmail, String prefRoom, String prefService) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_USER_NAME, newName);
        values.put(DatabaseHelper.COLUMN_USER_EMAIL, newEmail);
        values.put(DatabaseHelper.COLUMN_USER_PREF_ROOM, prefRoom);
        values.put(DatabaseHelper.COLUMN_USER_PREF_SERVICE, prefService);
        int updatedRows = db.update(DatabaseHelper.TABLE_USERS, values, DatabaseHelper.COLUMN_USER_EMAIL + " = ?", new String[]{originalEmail});
        db.close();
        return updatedRows > 0;
    }


    // --- Booking Management Methods ---

    public boolean addBooking(Booking booking) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_BOOKING_USER_EMAIL, booking.getUserEmail());
        values.put(DatabaseHelper.COLUMN_BOOKING_ITEM_NAME, booking.getItemName());
        values.put(DatabaseHelper.COLUMN_BOOKING_START_DATE, booking.getStartDate());
        values.put(DatabaseHelper.COLUMN_BOOKING_END_DATE, booking.getEndDate());
        values.put(DatabaseHelper.COLUMN_BOOKING_ITEM_TYPE, booking.getItemType());
        values.put(DatabaseHelper.COLUMN_BOOKING_STATUS, "Upcoming");
        values.put(DatabaseHelper.COLUMN_BOOKING_IMAGE_URI, booking.getImageUri());
        long result = db.insert(DatabaseHelper.TABLE_BOOKINGS, null, values);
        db.close();
        return result != -1;
    }

    public List<Booking> getBookingsForUser(String userEmail) {
        List<Booking> bookingList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_BOOKINGS, null, DatabaseHelper.COLUMN_BOOKING_USER_EMAIL + " = ?", new String[]{userEmail}, null, null, DatabaseHelper.COLUMN_BOOKING_START_DATE + " DESC");
        if (cursor.moveToFirst()) {
            do {
                Booking booking = new Booking(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOKING_ID)),
                        userEmail,
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOKING_ITEM_NAME)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOKING_START_DATE)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOKING_END_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOKING_ITEM_TYPE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOKING_STATUS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOKING_IMAGE_URI))
                );
                bookingList.add(booking);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return bookingList;
    }

    public boolean cancelBooking(int bookingId) {
        return updateBookingStatus(bookingId, "Cancelled");
    }

    public boolean updateBookingStatus(int bookingId, String newStatus) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_BOOKING_STATUS, newStatus);
        int updatedRows = db.update(DatabaseHelper.TABLE_BOOKINGS, values, DatabaseHelper.COLUMN_BOOKING_ID + " = ?", new String[]{String.valueOf(bookingId)});
        db.close();
        return updatedRows > 0;
    }

    public boolean updateBooking(int bookingId, long newStartDate, long newEndDate) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_BOOKING_START_DATE, newStartDate);
        values.put(DatabaseHelper.COLUMN_BOOKING_END_DATE, newEndDate);
        int updatedRows = db.update(DatabaseHelper.TABLE_BOOKINGS, values, DatabaseHelper.COLUMN_BOOKING_ID + " = ?", new String[]{String.valueOf(bookingId)});
        db.close();
        return updatedRows > 0;
    }


    // --- Availability Logic (New and Modified Bookings) ---

    private int getRoomBookedCount(String roomName, long checkInMillis, long checkOutMillis) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = "LOWER(" + DatabaseHelper.COLUMN_BOOKING_ITEM_NAME + ") = LOWER(?) AND " +
                DatabaseHelper.COLUMN_BOOKING_STATUS + " != ? AND " +
                DatabaseHelper.COLUMN_BOOKING_START_DATE + " < ? AND " +
                DatabaseHelper.COLUMN_BOOKING_END_DATE + " > ?";
        String[] selectionArgs = {roomName, "Cancelled", String.valueOf(checkOutMillis), String.valueOf(checkInMillis)};
        Cursor cursor = db.query(DatabaseHelper.TABLE_BOOKINGS, new String[]{DatabaseHelper.COLUMN_BOOKING_ID}, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    public boolean isRoomAvailable(String roomName, long checkInMillis, long checkOutMillis) {
        Room room = getRoomByName(roomName);
        if (room == null) return false;
        int capacity = room.getCapacity();
        int bookedCount = getRoomBookedCount(roomName, checkInMillis, checkOutMillis);
        return bookedCount < capacity;
    }

    private int getRoomBookedCountForModification(String roomName, long checkInMillis, long checkOutMillis, int bookingIdToExclude) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = "LOWER(" + DatabaseHelper.COLUMN_BOOKING_ITEM_NAME + ") = LOWER(?) AND " +
                DatabaseHelper.COLUMN_BOOKING_STATUS + " != ? AND " +
                DatabaseHelper.COLUMN_BOOKING_START_DATE + " < ? AND " +
                DatabaseHelper.COLUMN_BOOKING_END_DATE + " > ? AND " +
                DatabaseHelper.COLUMN_BOOKING_ID + " != ?";
        String[] selectionArgs = {roomName, "Cancelled", String.valueOf(checkOutMillis), String.valueOf(checkInMillis), String.valueOf(bookingIdToExclude)};
        Cursor cursor = db.query(DatabaseHelper.TABLE_BOOKINGS, new String[]{DatabaseHelper.COLUMN_BOOKING_ID}, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    public boolean isRoomAvailableForModification(String roomName, long checkInMillis, long checkOutMillis, int bookingIdToExclude) {
        Room room = getRoomByName(roomName);
        if (room == null) return false;
        int capacity = room.getCapacity();
        int bookedCount = getRoomBookedCountForModification(roomName, checkInMillis, checkOutMillis, bookingIdToExclude);
        return bookedCount < capacity;
    }

    private int getServiceBookedCount(String serviceName, long startMillis) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = "LOWER(" + DatabaseHelper.COLUMN_BOOKING_ITEM_NAME + ") = LOWER(?) AND " +
                DatabaseHelper.COLUMN_BOOKING_START_DATE + " = ? AND " +
                DatabaseHelper.COLUMN_BOOKING_STATUS + " != ?";
        String[] selectionArgs = { serviceName, String.valueOf(startMillis), "Cancelled" };
        Cursor cursor = db.query(DatabaseHelper.TABLE_BOOKINGS, new String[]{DatabaseHelper.COLUMN_BOOKING_ID}, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    public boolean isServiceSlotAvailable(String serviceName, long startMillis) {
        Service service = getServiceByName(serviceName);
        if (service == null) return false;
        int capacity = service.getCapacity();
        int bookedCount = getServiceBookedCount(serviceName, startMillis);
        return bookedCount < capacity;
    }

    private int getServiceBookedCountForModification(String serviceName, long startMillis, int bookingIdToExclude) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = "LOWER(" + DatabaseHelper.COLUMN_BOOKING_ITEM_NAME + ") = LOWER(?) AND " +
                DatabaseHelper.COLUMN_BOOKING_START_DATE + " = ? AND " +
                DatabaseHelper.COLUMN_BOOKING_STATUS + " != ? AND " +
                DatabaseHelper.COLUMN_BOOKING_ID + " != ?";
        String[] selectionArgs = { serviceName, String.valueOf(startMillis), "Cancelled", String.valueOf(bookingIdToExclude) };
        Cursor cursor = db.query(DatabaseHelper.TABLE_BOOKINGS, new String[]{DatabaseHelper.COLUMN_BOOKING_ID}, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    public boolean isServiceSlotAvailableForModification(String serviceName, long startMillis, int bookingIdToExclude) {
        Service service = getServiceByName(serviceName);
        if (service == null) return false;
        int capacity = service.getCapacity();
        int bookedCount = getServiceBookedCountForModification(serviceName, startMillis, bookingIdToExclude);
        return bookedCount < capacity;
    }


    public List<Booking> getTodaysBookings() {
        List<Booking> dailyBookings = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long startOfDay = calendar.getTimeInMillis();

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        long endOfDay = calendar.getTimeInMillis();

        String selection = DatabaseHelper.COLUMN_BOOKING_START_DATE + " BETWEEN ? AND ? AND " +
                DatabaseHelper.COLUMN_BOOKING_STATUS + " != ?";
        String[] selectionArgs = {String.valueOf(startOfDay), String.valueOf(endOfDay), "Cancelled"};
        Cursor cursor = db.query(DatabaseHelper.TABLE_BOOKINGS, null, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Booking booking = new Booking(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOKING_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOKING_USER_EMAIL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOKING_ITEM_NAME)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOKING_START_DATE)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOKING_END_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOKING_ITEM_TYPE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOKING_STATUS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOKING_IMAGE_URI))
                );
                dailyBookings.add(booking);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return dailyBookings;
    }

    // --- Room Management Methods (Admin) ---

    public boolean addRoom(Room room) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ROOM_NAME, room.getName());
        values.put(DatabaseHelper.COLUMN_ROOM_DESCRIPTION, room.getDescription());
        values.put(DatabaseHelper.COLUMN_ROOM_PRICE, room.getPrice());
        values.put(DatabaseHelper.COLUMN_ROOM_AMENITIES, room.getAmenities());
        values.put(DatabaseHelper.COLUMN_ROOM_CAPACITY, room.getCapacity());
        values.put(DatabaseHelper.COLUMN_ROOM_IMAGE_URI, room.getImageUri());
        long result = db.insert(DatabaseHelper.TABLE_ROOMS, null, values);
        db.close();
        return result != -1;
    }

    public List<Room> getAllRooms() {
        List<Room> roomList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_ROOMS, null);
        if (cursor.moveToFirst()) {
            do {
                Room room = new Room(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ROOM_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ROOM_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ROOM_DESCRIPTION)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ROOM_PRICE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ROOM_AMENITIES)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ROOM_CAPACITY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ROOM_IMAGE_URI))
                );
                roomList.add(room);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return roomList;
    }

    public Room getRoomById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_ROOMS, null, DatabaseHelper.COLUMN_ROOM_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);
        Room room = null;
        if (cursor != null && cursor.moveToFirst()) {
            room = new Room(
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ROOM_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ROOM_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ROOM_DESCRIPTION)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ROOM_PRICE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ROOM_AMENITIES)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ROOM_CAPACITY)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ROOM_IMAGE_URI))
            );
        }
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return room;
    }

    public Room getRoomByName(String name) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_ROOMS, null, "LOWER(" + DatabaseHelper.COLUMN_ROOM_NAME + ") = LOWER(?)", new String[]{name}, null, null, null);
        Room room = null;
        if (cursor != null && cursor.moveToFirst()) {
            room = new Room(
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ROOM_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ROOM_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ROOM_DESCRIPTION)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ROOM_PRICE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ROOM_AMENITIES)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ROOM_CAPACITY)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ROOM_IMAGE_URI))
            );
        }
        if (cursor != null) cursor.close();
        db.close();
        return room;
    }

    public boolean updateRoom(Room room) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ROOM_NAME, room.getName());
        values.put(DatabaseHelper.COLUMN_ROOM_DESCRIPTION, room.getDescription());
        values.put(DatabaseHelper.COLUMN_ROOM_PRICE, room.getPrice());
        values.put(DatabaseHelper.COLUMN_ROOM_AMENITIES, room.getAmenities());
        values.put(DatabaseHelper.COLUMN_ROOM_CAPACITY, room.getCapacity());
        values.put(DatabaseHelper.COLUMN_ROOM_IMAGE_URI, room.getImageUri());
        int updatedRows = db.update(DatabaseHelper.TABLE_ROOMS, values, DatabaseHelper.COLUMN_ROOM_ID + " = ?", new String[]{String.valueOf(room.getId())});
        db.close();
        return updatedRows > 0;
    }

    public boolean deleteRoom(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int deletedRows = db.delete(DatabaseHelper.TABLE_ROOMS, DatabaseHelper.COLUMN_ROOM_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return deletedRows > 0;
    }


    // --- Service Management Methods (Admin) ---

    public boolean addService(Service service) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_SERVICE_NAME, service.getName());
        values.put(DatabaseHelper.COLUMN_SERVICE_DESCRIPTION, service.getDescription());
        values.put(DatabaseHelper.COLUMN_SERVICE_PRICE, service.getPrice());
        values.put(DatabaseHelper.COLUMN_SERVICE_CAPACITY, service.getCapacity());
        values.put(DatabaseHelper.COLUMN_SERVICE_IMAGE_URI, service.getImageUri());
        long result = db.insert(DatabaseHelper.TABLE_SERVICES, null, values);
        db.close();
        return result != -1;
    }

    public List<Service> getAllServices() {
        List<Service> serviceList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_SERVICES, null);
        if (cursor.moveToFirst()) {
            do {
                Service service = new Service(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SERVICE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SERVICE_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SERVICE_DESCRIPTION)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SERVICE_PRICE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SERVICE_CAPACITY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SERVICE_IMAGE_URI))
                );
                serviceList.add(service);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return serviceList;
    }

    public Service getServiceById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_SERVICES, null, DatabaseHelper.COLUMN_SERVICE_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);
        Service service = null;
        if (cursor != null && cursor.moveToFirst()) {
            service = new Service(
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SERVICE_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SERVICE_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SERVICE_DESCRIPTION)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SERVICE_PRICE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SERVICE_CAPACITY)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SERVICE_IMAGE_URI))
            );
        }
        if (cursor != null) cursor.close();
        db.close();
        return service;
    }

    public Service getServiceByName(String name) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_SERVICES, null, "LOWER(" + DatabaseHelper.COLUMN_SERVICE_NAME + ") = LOWER(?)", new String[]{name}, null, null, null);
        Service service = null;
        if (cursor != null && cursor.moveToFirst()) {
            service = new Service(
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SERVICE_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SERVICE_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SERVICE_DESCRIPTION)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SERVICE_PRICE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SERVICE_CAPACITY)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SERVICE_IMAGE_URI))
            );
        }
        if (cursor != null) cursor.close();
        db.close();
        return service;
    }

    public boolean updateService(Service service) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_SERVICE_NAME, service.getName());
        values.put(DatabaseHelper.COLUMN_SERVICE_DESCRIPTION, service.getDescription());
        values.put(DatabaseHelper.COLUMN_SERVICE_PRICE, service.getPrice());
        values.put(DatabaseHelper.COLUMN_SERVICE_CAPACITY, service.getCapacity());
        values.put(DatabaseHelper.COLUMN_SERVICE_IMAGE_URI, service.getImageUri());
        int updatedRows = db.update(DatabaseHelper.TABLE_SERVICES, values, DatabaseHelper.COLUMN_SERVICE_ID + " = ?", new String[]{String.valueOf(service.getId())});
        db.close();
        return updatedRows > 0;
    }

    public boolean deleteService(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int deletedRows = db.delete(DatabaseHelper.TABLE_SERVICES, DatabaseHelper.COLUMN_SERVICE_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return deletedRows > 0;
    }
}