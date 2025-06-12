//
//  NetworkMonitor.swift
//  iosApp
//
//  Created by Tomi EEDF on 11/06/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import Foundation
import Network

@objc public class NetworkMonitor: NSObject {
    
    private let monitor = NWPathMonitor()
    private let queue = DispatchQueue(label: "NetworkMonitorQueue")

    @objc dynamic private(set) var isConnected: Bool = false
    
    // Add a callback closure
    var onStatusChange: ((Bool) -> Void)?

    override init() {
        super.init()
        monitor.pathUpdateHandler = { [weak self] path in
            let status = path.status == .satisfied
            DispatchQueue.main.async {
                self?.isConnected = status
                self?.onStatusChange?(status)  // notify listener
            }
        }
        monitor.start(queue: queue)
    }

    deinit {
        monitor.cancel()
    }
}
