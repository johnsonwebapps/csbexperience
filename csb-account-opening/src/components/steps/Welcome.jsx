import { useState } from 'react'

const PURPOSE_OPTIONS = [
  {
    id: 'new-business',
    icon: '🏢',
    title: 'New Business',
    desc: 'Opening accounts for a newly formed business entity',
  },
  {
    id: 'existing-business',
    icon: '📈',
    title: 'Existing Business',
    desc: 'Adding or replacing accounts for an established business',
  },
  {
    id: 'sole-proprietor',
    icon: '👤',
    title: 'Sole Proprietor',
    desc: 'Self-employed individual or single-member LLC without employees',
  },
]

export default function Welcome({ formData, updateFormData, onNext }) {
  const [error, setError] = useState('')

  const handleNext = () => {
    if (!formData.accountPurpose) {
      setError('Please select an option to continue.')
      return
    }
    setError('')
    onNext()
  }

  return (
    <div className="space-y-6">
      {/* Hero banner */}
      <div className="rounded-xl overflow-hidden" style={{ background: 'linear-gradient(135deg, #003366 0%, #005599 100%)' }}>
        <div className="p-8 text-white">
          <div className="inline-block px-3 py-1 bg-csb-gold text-csb-navy text-xs font-bold rounded-full mb-3 uppercase tracking-wider">
            Small Business Banking
          </div>
          <h1 className="text-3xl font-bold mb-2 leading-tight">
            Open Your Business Account
          </h1>
          <p className="text-blue-100 text-lg max-w-xl">
            Get started in minutes. Our secure online application makes it easy
            to open the right account for your business.
          </p>
          <div className="flex flex-wrap gap-4 mt-5 text-sm">
            <div className="flex items-center gap-2">
              <span className="text-csb-gold">✓</span>
              <span>FDIC Insured</span>
            </div>
            <div className="flex items-center gap-2">
              <span className="text-csb-gold">✓</span>
              <span>No hidden fees</span>
            </div>
            <div className="flex items-center gap-2">
              <span className="text-csb-gold">✓</span>
              <span>Local, community bank</span>
            </div>
            <div className="flex items-center gap-2">
              <span className="text-csb-gold">✓</span>
              <span>15–20 minutes to complete</span>
            </div>
          </div>
        </div>
      </div>

      {/* What you'll need */}
      <div className="card">
        <h2 className="text-lg font-bold text-csb-navy mb-3">Before You Begin</h2>
        <p className="text-gray-600 text-sm mb-4">
          Please have the following information ready to complete your application:
        </p>
        <div className="grid sm:grid-cols-2 gap-3 text-sm">
          {[
            ['📄', 'Business formation documents (Articles of Incorporation, LLC Agreement, etc.)'],
            ['🔢', 'Employer Identification Number (EIN) or Tax ID'],
            ['🪪', 'Government-issued photo ID for all owners'],
            ['📊', 'SSN/ITIN for owners with 25% or more ownership'],
            ['🏠', 'Business and personal addresses'],
            ['💳', 'Initial deposit information (if funding at opening)'],
          ].map(([icon, text], i) => (
            <div key={i} className="flex items-start gap-2 bg-gray-50 rounded-lg p-3">
              <span className="text-lg flex-shrink-0">{icon}</span>
              <span className="text-gray-700">{text}</span>
            </div>
          ))}
        </div>
      </div>

      {/* Purpose selection */}
      <div className="card">
        <h2 className="section-header">What brings you here today?</h2>
        <p className="text-gray-500 text-sm mb-5">
          Tell us a little about your situation so we can guide you through the right process.
        </p>
        <div className="space-y-3">
          {PURPOSE_OPTIONS.map(opt => (
            <div
              key={opt.id}
              className={`product-card flex items-start gap-4 ${formData.accountPurpose === opt.id ? 'selected' : ''}`}
              onClick={() => { updateFormData({ accountPurpose: opt.id }); setError('') }}
              role="radio"
              aria-checked={formData.accountPurpose === opt.id}
              tabIndex={0}
              onKeyDown={e => { if (e.key === 'Enter' || e.key === ' ') { updateFormData({ accountPurpose: opt.id }); setError('') } }}
            >
              <div className="text-3xl mt-0.5">{opt.icon}</div>
              <div className="flex-1">
                <div className="font-semibold text-gray-800">{opt.title}</div>
                <div className="text-sm text-gray-500 mt-0.5">{opt.desc}</div>
              </div>
              <div className={`mt-1 w-5 h-5 rounded-full border-2 flex items-center justify-center flex-shrink-0 transition-all ${
                formData.accountPurpose === opt.id
                  ? 'border-csb-navy'
                  : 'border-gray-300'
              }`}>
                {formData.accountPurpose === opt.id && (
                  <div className="w-2.5 h-2.5 rounded-full bg-csb-navy" />
                )}
              </div>
            </div>
          ))}
        </div>
        {error && <p className="error-text mt-2">{error}</p>}
      </div>

      {/* Disclosures */}
      <div className="text-xs text-gray-400 leading-relaxed">
        Cambridge Savings Bank is a Member FDIC. By proceeding, you agree to our{' '}
        <a href="#" className="underline hover:text-gray-600">Privacy Policy</a> and{' '}
        <a href="#" className="underline hover:text-gray-600">Terms of Use</a>. This application is for
        business accounts only. For personal accounts, please visit your nearest branch or call us.
      </div>

      <div className="flex justify-end">
        <button className="btn-primary px-8" onClick={handleNext}>
          Continue →
        </button>
      </div>
    </div>
  )
}
