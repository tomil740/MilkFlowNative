package com.tomiappdevelopment.milk_flow.data.remote.dtoModels

data class PagedDemandsDto(
    val demands: List<DemandDto>,
    val nextPageToken: String?
)
