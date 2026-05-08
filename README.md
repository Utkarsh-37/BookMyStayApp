BookMyStay Hotel Booking App A modular, console-based Java application that simulates hotel booking operations. The app evolves through sequential use cases (UC1–UC6), each extending functionality while preserving prior features.

UC1: Room Inventory Setup Goal: Allow Admin to define room types, counts, and prices. Description: Admin can add new room categories (Single, Double, Suite), set availability, and assign base prices. This establishes the foundation for all booking operations.

UC2: Guest Search & Availability Goal: Enable Guests to view and check room availability. Description: Guests can browse the current inventory, check specific room types, and see pricing. This provides transparency before booking requests.

UC3: Booking Request Queue Goal: Allow Guests to place booking requests. Description: Guests submit booking requests that are stored in a queue. Requests remain pending until processed by Admin, ensuring fairness and order.

UC4: Reservation Confirmation & Room Allocation Goal: Admin confirms bookings and allocates unique room IDs. Description: Admin processes requests from the queue, assigns room IDs, and updates inventory atomically. This prevents double-booking and ensures reliable allocation.

UC5: Add-On Service Selection Goal: Guests can enrich bookings with optional services. Description: During booking requests, Guests select add-ons like Breakfast, Spa, or Airport Pickup. These services are attached to reservations and displayed upon confirmation.

UC6: Booking History & Reporting Goal: Maintain a complete audit trail of confirmed reservations. Description: Confirmed bookings are stored in a history list. Admin can review past reservations, cancel bookings, and generate reports (total bookings, breakdown by room type). This supports customer service and reporting needs.
