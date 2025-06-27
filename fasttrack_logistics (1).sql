-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jun 07, 2025 at 07:19 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `fasttrack_logistics`
--

-- --------------------------------------------------------

--
-- Table structure for table `deliveries`
--

CREATE TABLE `deliveries` (
  `delivery_id` int(11) NOT NULL,
  `shipment_id` int(11) NOT NULL,
  `personnel_id` int(11) DEFAULT NULL,
  `scheduled_pickup_time` datetime DEFAULT NULL,
  `actual_pickup_time` datetime DEFAULT NULL,
  `scheduled_delivery_time` datetime DEFAULT NULL,
  `actual_delivery_time` datetime DEFAULT NULL,
  `delivery_status` varchar(50) NOT NULL DEFAULT 'Scheduled',
  `route_details` text DEFAULT NULL,
  `delivery_notes` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `deliveries`
--

INSERT INTO `deliveries` (`delivery_id`, `shipment_id`, `personnel_id`, `scheduled_pickup_time`, `actual_pickup_time`, `scheduled_delivery_time`, `actual_delivery_time`, `delivery_status`, `route_details`, `delivery_notes`) VALUES
(1, 1, 1, '2024-04-04 10:40:23', '2024-04-04 10:40:23', '2024-04-04 10:40:23', '2024-04-04 10:40:23', 'Out for Delivery', 'fghj', 'cxxx'),
(2, 2, 1, '2025-05-23 22:25:45', NULL, '2024-04-04 10:40:23', NULL, 'Scheduled', 'Auto-assigned route', 'Assigned automatically through system.');

-- --------------------------------------------------------

--
-- Table structure for table `delivery_personnel`
--

CREATE TABLE `delivery_personnel` (
  `personnel_id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `contact_info` varchar(255) NOT NULL,
  `personnel_type` varchar(50) DEFAULT 'Driver',
  `license_number` varchar(100) DEFAULT NULL,
  `vehicle_details` text DEFAULT NULL,
  `availability_status` varchar(50) NOT NULL DEFAULT 'Available'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `delivery_personnel`
--

INSERT INTO `delivery_personnel` (`personnel_id`, `name`, `contact_info`, `personnel_type`, `license_number`, `vehicle_details`, `availability_status`) VALUES
(1, 'kamal', '3333', 'Driver', '334', '333', 'On Duty');

-- --------------------------------------------------------

--
-- Table structure for table `notifications`
--

CREATE TABLE `notifications` (
  `notification_id` int(11) NOT NULL,
  `recipient_type` varchar(50) NOT NULL,
  `recipient_id` int(11) DEFAULT NULL,
  `related_shipment_id` int(11) DEFAULT NULL,
  `related_personnel_id` int(11) DEFAULT NULL,
  `message` text NOT NULL,
  `notification_type` varchar(50) NOT NULL DEFAULT 'Status Update',
  `timestamp` datetime DEFAULT current_timestamp(),
  `is_read` tinyint(1) DEFAULT 0,
  `contact_method` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `notifications`
--

INSERT INTO `notifications` (`notification_id`, `recipient_type`, `recipient_id`, `related_shipment_id`, `related_personnel_id`, `message`, `notification_type`, `timestamp`, `is_read`, `contact_method`) VALUES
(1, 'Customer', 1, 1, NULL, 'fghjk', 'Status Update', '2025-05-23 22:19:43', 0, 'App'),
(2, 'Customer', 1, 1, NULL, 'uyfutryezyd', 'Status Update', '2025-05-23 22:34:08', 0, 'App'),
(3, 'Personnel', 1, NULL, NULL, 'yuio', 'Status Update', '2025-05-23 22:34:25', 0, 'App');

-- --------------------------------------------------------

--
-- Table structure for table `shipments`
--

CREATE TABLE `shipments` (
  `shipment_id` int(11) NOT NULL,
  `tracking_number` varchar(255) NOT NULL,
  `sender_name` varchar(255) NOT NULL,
  `sender_address` text NOT NULL,
  `sender_contact` varchar(255) DEFAULT NULL,
  `receiver_name` varchar(255) NOT NULL,
  `receiver_address` text NOT NULL,
  `receiver_contact` varchar(255) DEFAULT NULL,
  `package_contents` text DEFAULT NULL,
  `weight` decimal(10,2) DEFAULT NULL,
  `dimensions` varchar(255) DEFAULT NULL,
  `delivery_status` varchar(50) NOT NULL DEFAULT 'Pending',
  `current_location` varchar(255) DEFAULT NULL,
  `estimated_delivery_time` datetime DEFAULT NULL,
  `actual_delivery_time` datetime DEFAULT NULL,
  `special_instructions` text DEFAULT NULL,
  `creation_date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `shipments`
--

INSERT INTO `shipments` (`shipment_id`, `tracking_number`, `sender_name`, `sender_address`, `sender_contact`, `receiver_name`, `receiver_address`, `receiver_contact`, `package_contents`, `weight`, `dimensions`, `delivery_status`, `current_location`, `estimated_delivery_time`, `actual_delivery_time`, `special_instructions`, `creation_date`) VALUES
(1, '456', 'nejana', 'dfg', '333', 'dfg', 'ddd', '456', 'gff', 45.00, '5x5x5', 'Out for Delivery', 'fgh', '2024-04-04 10:40:23', NULL, 'ffgg', '2025-05-23 16:45:06'),
(2, '567', 'hjk', 'dfgg', '678', 'tyu', 'erty', '890', 'hh', 89.00, '5x6x6', 'Scheduled', 'jhvv', '2024-04-04 10:40:23', NULL, 'gfds', '2025-05-23 16:55:21');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `deliveries`
--
ALTER TABLE `deliveries`
  ADD PRIMARY KEY (`delivery_id`),
  ADD KEY `shipment_id` (`shipment_id`),
  ADD KEY `personnel_id` (`personnel_id`);

--
-- Indexes for table `delivery_personnel`
--
ALTER TABLE `delivery_personnel`
  ADD PRIMARY KEY (`personnel_id`),
  ADD UNIQUE KEY `license_number` (`license_number`);

--
-- Indexes for table `notifications`
--
ALTER TABLE `notifications`
  ADD PRIMARY KEY (`notification_id`),
  ADD KEY `related_shipment_id` (`related_shipment_id`),
  ADD KEY `related_personnel_id` (`related_personnel_id`);

--
-- Indexes for table `shipments`
--
ALTER TABLE `shipments`
  ADD PRIMARY KEY (`shipment_id`),
  ADD UNIQUE KEY `tracking_number` (`tracking_number`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `deliveries`
--
ALTER TABLE `deliveries`
  MODIFY `delivery_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `delivery_personnel`
--
ALTER TABLE `delivery_personnel`
  MODIFY `personnel_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `notifications`
--
ALTER TABLE `notifications`
  MODIFY `notification_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `shipments`
--
ALTER TABLE `shipments`
  MODIFY `shipment_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `deliveries`
--
ALTER TABLE `deliveries`
  ADD CONSTRAINT `deliveries_ibfk_1` FOREIGN KEY (`shipment_id`) REFERENCES `shipments` (`shipment_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `deliveries_ibfk_2` FOREIGN KEY (`personnel_id`) REFERENCES `delivery_personnel` (`personnel_id`) ON DELETE SET NULL;

--
-- Constraints for table `notifications`
--
ALTER TABLE `notifications`
  ADD CONSTRAINT `notifications_ibfk_1` FOREIGN KEY (`related_shipment_id`) REFERENCES `shipments` (`shipment_id`) ON DELETE SET NULL,
  ADD CONSTRAINT `notifications_ibfk_2` FOREIGN KEY (`related_personnel_id`) REFERENCES `delivery_personnel` (`personnel_id`) ON DELETE SET NULL;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
