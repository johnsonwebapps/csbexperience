function CheckIcon() {
  return (
    <svg className="w-4 h-4" viewBox="0 0 20 20" fill="currentColor">
      <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd" />
    </svg>
  )
}

export default function ProgressSidebar({ steps, currentStep }) {
  return (
    <div className="card sticky top-4">
      <div className="text-xs font-bold uppercase tracking-widest text-gray-400 mb-4">Application Progress</div>
      <ol className="space-y-1">
        {steps.map((step, i) => {
          const status = step.id < currentStep ? 'completed' : step.id === currentStep ? 'active' : 'pending'
          return (
            <li key={step.id} className="flex items-start gap-3">
              <div className="flex flex-col items-center">
                <div className={`step-dot ${status}`}>
                  {status === 'completed' ? <CheckIcon /> : step.id}
                </div>
                {i < steps.length - 1 && (
                  <div className={`w-0.5 h-6 mt-1 rounded ${status === 'completed' ? 'bg-csb-green' : 'bg-gray-200'}`} />
                )}
              </div>
              <div className="pt-1.5 pb-3">
                <div className={`text-sm font-semibold leading-tight ${
                  status === 'active' ? 'text-csb-navy' :
                  status === 'completed' ? 'text-csb-green' :
                  'text-gray-400'
                }`}>
                  {step.label}
                </div>
                {status === 'active' && (
                  <div className="text-xs text-gray-500 mt-0.5">In progress</div>
                )}
                {status === 'completed' && (
                  <div className="text-xs text-csb-green mt-0.5">Complete</div>
                )}
              </div>
            </li>
          )
        })}
      </ol>

      <div className="mt-4 pt-4 border-t border-gray-100">
        <div className="info-box">
          <p className="font-semibold mb-1">Need help?</p>
          <p>Call us at <a href="tel:1-888-418-5626" className="font-bold text-csb-navy">1-888-418-5626</a></p>
          <p className="mt-1">Mon–Fri 8am–6pm ET</p>
        </div>
      </div>
    </div>
  )
}
