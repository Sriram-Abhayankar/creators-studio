/**
 * DTO Request package for Creators Studio.
 *
 * Contains request body objects used for API data transfer.
 * These DTOs define the structure of incoming API requests
 * and prevent direct entity exposure.
 *
 * Planned DTOs for Phase 2:
 * - UserRegisterRequest (username, password, confirmPassword)
 * - UserLoginRequest (username, password)
 * - FabricPurchaseRequest (fabricName, fabricType, list of fabric item rows)
 * - FabricItemRequest (colour, gsm, weight, rib, pricePerKg)
 * - AccessoryPurchaseRequest (accessoryName, cone/sizePattern/others data)
 * - ConeItemRequest (colourName, colourCode, unit, pricePerUnit)
 * - SizePatternRequest (brandName, styleNumber, styleName, price)
 * - OthersRequest (itemsName, unit, price)
 */
package com.creatorsstudio.dto.request;
