import UIKit
import SwiftUI
import Shared

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        
        ComposeView()
            .edgesIgnoringSafeArea(.all)
            .background(Color.white)
                .ignoresSafeArea(.keyboard) // Compose has own keyboard handler
    }
}



