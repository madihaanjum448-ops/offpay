# OFFPAY Architecture

## Part 3 — Offline Payment Infrastructure

This section describes the firmware and physical-card payment path of OFFPAY.

## Physical Payment Flow

Customer
→ Offline Voucher
→ RFID/MIFARE Card
→ RC522 Reader
→ ESP32
→ Merchant Device
→ Backend
→ Blockchain Settlement

## APDU

The active phone-to-phone NFC payment command is:

`0xC1`

## Hardware Components

- ESP32
- RC522 RFID reader
- MIFARE card

## Firmware Responsibilities

- Read payment data from the RFID card
- Write payment data to the RFID card
- Handle card state
- Generate hardware endorsement
- Communicate with the merchant device
- Protect device key material

## Security

ESP32 flash encryption should be enabled as part of the firmware security configuration.

## Hardware Endorsement

The hardware endorsement must use the canonical OFFPAY endorsement format defined by the project protocol.

The endorsement implementation must remain compatible with the blockchain and Android implementations.