export default function Header() {
  return (
    <header className="bg-csb-navy shadow-md">
      <div className="max-w-6xl mx-auto px-4 py-0">
        <div className="flex items-center justify-between h-16">
          <div className="flex items-center gap-3">
            {/* CSB Logo mark */}
            <div className="flex items-center gap-2">
              <div className="w-9 h-9 rounded-full bg-csb-gold flex items-center justify-center">
                <span className="text-csb-navy font-black text-sm">CSB</span>
              </div>
              <div>
                <div className="text-white font-bold text-sm leading-tight">Cambridge Savings Bank</div>
                <div className="text-blue-200 text-xs leading-tight">Open a Business Account</div>
              </div>
            </div>
          </div>
          <div className="flex items-center gap-4">
            <a href="tel:1-888-418-5626" className="text-blue-200 text-sm hidden sm:flex items-center gap-1 hover:text-white transition-colors">
              <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z" />
              </svg>
              1-888-418-5626
            </a>
            <a href="#" className="text-blue-200 text-sm hover:text-white transition-colors">Sign In</a>
          </div>
        </div>
      </div>
      {/* Green accent bar */}
      <div className="h-1 bg-csb-green" />
    </header>
  )
}
