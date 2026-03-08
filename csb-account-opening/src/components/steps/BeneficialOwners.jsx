import { useState } from 'react'

const US_STATES = [
  'AL','AK','AZ','AR','CA','CO','CT','DE','FL','GA','HI','ID','IL','IN','IA',
  'KS','KY','LA','ME','MD','MA','MI','MN','MS','MO','MT','NE','NV','NH','NJ',
  'NM','NY','NC','ND','OH','OK','OR','PA','RI','SC','SD','TN','TX','UT','VT',
  'VA','WA','WV','WI','WY','DC'
]

const MAX_DOB = new Date(Date.now() - 18 * 365.25 * 24 * 60 * 60 * 1000).toISOString().split('T')[0]

const EMPTY_OWNER = {
  firstName: '', lastName: '', title: '', ownershipPct: '',
  dob: '', ssn: '', email: '',
  address: '', city: '', state: '', zip: '',
}

function Field({ label, required, error, hint, children }) {
  return (
    <div>
      <label className="form-label">
        {label}
        {required && <span className="required-star ml-1">*</span>}
      </label>
      {children}
      {hint && <p className="text-xs text-gray-400 mt-1">{hint}</p>}
      {error && <p className="error-text">{error}</p>}
    </div>
  )
}

function OwnerForm({ owner, index, onUpdate, onRemove, errors }) {
  const [showSSN, setShowSSN] = useState(false)

  const set = (field) => (e) => onUpdate(index, { ...owner, [field]: e.target.value })

  const formatSSN = (val) => {
    const d = val.replace(/\D/g, '').slice(0, 9)
    if (d.length >= 6) return `${d.slice(0,3)}-${d.slice(3,5)}-${d.slice(5)}`
    if (d.length >= 4) return `${d.slice(0,3)}-${d.slice(3)}`
    return d
  }

  const e = errors || {}

  return (
    <div className="border border-gray-200 rounded-xl p-5 bg-gray-50 space-y-4">
      <div className="flex items-center justify-between mb-1">
        <h4 className="font-bold text-csb-navy">Beneficial Owner #{index + 1}</h4>
        <button
          type="button"
          onClick={() => onRemove(index)}
          className="text-red-500 hover:text-red-700 text-sm font-medium flex items-center gap-1"
        >
          <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
          </svg>
          Remove
        </button>
      </div>

      <div className="grid sm:grid-cols-2 gap-4">
        <Field label="First Name" required error={e.firstName}>
          <input type="text" className="form-input" placeholder="First name" value={owner.firstName} onChange={set('firstName')} />
        </Field>
        <Field label="Last Name" required error={e.lastName}>
          <input type="text" className="form-input" placeholder="Last name" value={owner.lastName} onChange={set('lastName')} />
        </Field>
      </div>

      <div className="grid sm:grid-cols-2 gap-4">
        <Field label="Title / Role" required error={e.title}>
          <input type="text" className="form-input" placeholder="e.g., Co-Owner, Partner, Director" value={owner.title} onChange={set('title')} />
        </Field>
        <Field label="Ownership %" required error={e.ownershipPct} hint="Must be 25% or more">
          <div className="relative">
            <input
              type="number" className="form-input pr-8" placeholder="e.g., 30"
              min="25" max="100" value={owner.ownershipPct}
              onChange={set('ownershipPct')}
            />
            <span className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 font-medium">%</span>
          </div>
        </Field>
      </div>

      <div className="grid sm:grid-cols-2 gap-4">
        <Field label="Date of Birth" required error={e.dob}>
          <input
            type="date" className="form-input" value={owner.dob} onChange={set('dob')}
            max={MAX_DOB}
          />
        </Field>
        <Field label="Social Security Number" required error={e.ssn}>
          <div className="relative">
            <input
              type={showSSN ? 'text' : 'password'}
              className="form-input pr-12"
              placeholder="XXX-XX-XXXX"
              value={owner.ssn}
              onChange={e2 => onUpdate(index, { ...owner, ssn: formatSSN(e2.target.value) })}
            />
            <button
              type="button"
              className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 text-xs font-medium"
              onClick={() => setShowSSN(v => !v)}
            >
              {showSSN ? 'Hide' : 'Show'}
            </button>
          </div>
        </Field>
      </div>

      <Field label="Email Address" required error={e.email}>
        <input type="email" className="form-input" placeholder="owner@example.com" value={owner.email} onChange={set('email')} />
      </Field>

      <Field label="Home Address" required error={e.address}>
        <input type="text" className="form-input" placeholder="Street address" value={owner.address} onChange={set('address')} />
      </Field>
      <div className="grid sm:grid-cols-3 gap-4">
        <Field label="City" required error={e.city}>
          <input type="text" className="form-input" placeholder="City" value={owner.city} onChange={set('city')} />
        </Field>
        <Field label="State" required error={e.state}>
          <select className="form-input" value={owner.state} onChange={set('state')}>
            <option value="">State</option>
            {US_STATES.map(s => <option key={s} value={s}>{s}</option>)}
          </select>
        </Field>
        <Field label="ZIP" required error={e.zip}>
          <input type="text" className="form-input" placeholder="02138" value={owner.zip} onChange={set('zip')} maxLength={10} />
        </Field>
      </div>
    </div>
  )
}

export default function BeneficialOwners({ formData, updateFormData, onNext, onBack }) {
  const [errors, setErrors] = useState({})
  const [ownerErrors, setOwnerErrors] = useState([])
  const owners = formData.beneficialOwners

  const addOwner = () => {
    updateFormData({ beneficialOwners: [...owners, { ...EMPTY_OWNER }] })
  }

  const updateOwner = (index, updated) => {
    const newOwners = [...owners]
    newOwners[index] = updated
    updateFormData({ beneficialOwners: newOwners })
  }

  const removeOwner = (index) => {
    const newOwners = owners.filter((_, i) => i !== index)
    updateFormData({ beneficialOwners: newOwners })
  }

  const validateOwner = (o) => {
    const e = {}
    if (!o.firstName.trim()) e.firstName = 'Required.'
    if (!o.lastName.trim()) e.lastName = 'Required.'
    if (!o.title.trim()) e.title = 'Required.'
    if (!o.ownershipPct) e.ownershipPct = 'Required.'
    else {
      const pct = parseFloat(o.ownershipPct)
      if (pct < 25) e.ownershipPct = 'Must be 25% or more.'
    }
    if (!o.dob) e.dob = 'Required.'
    if (!o.ssn.trim()) e.ssn = 'Required.'
    else if (!/^\d{3}-\d{2}-\d{4}$/.test(o.ssn)) e.ssn = 'Invalid SSN format.'
    if (!o.email.trim()) e.email = 'Required.'
    else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(o.email)) e.email = 'Invalid email.'
    if (!o.address.trim()) e.address = 'Required.'
    if (!o.city.trim()) e.city = 'Required.'
    if (!o.state) e.state = 'Required.'
    if (!o.zip.trim()) e.zip = 'Required.'
    return e
  }

  const handleNext = () => {
    const newErrors = {}
    if (!formData.certifyBeneficialOwners) {
      newErrors.certify = 'You must certify the beneficial ownership information.'
    }

    const oErrors = owners.map(o => validateOwner(o))
    const hasOwnerErrors = oErrors.some(e => Object.keys(e).length > 0)

    setErrors(newErrors)
    setOwnerErrors(oErrors)

    if (Object.keys(newErrors).length === 0 && !hasOwnerErrors) {
      onNext()
    }
  }

  // Calculate total ownership including primary applicant
  const primaryPct = parseFloat(formData.ownershipPct) || 0
  const ownerPcts = owners.map(o => parseFloat(o.ownershipPct) || 0)
  const totalPct = primaryPct + ownerPcts.reduce((a, b) => a + b, 0)

  return (
    <div className="space-y-6">
      <div className="card">
        <h2 className="section-header">Beneficial Ownership</h2>
        <p className="text-gray-500 text-sm">
          Federal law requires us to identify and verify the identity of all beneficial owners — 
          individuals who own 25% or more of the business.
        </p>
      </div>

      {/* Regulation explanation */}
      <div className="card">
        <div className="flex gap-3">
          <div className="text-2xl flex-shrink-0">📋</div>
          <div>
            <h3 className="font-bold text-gray-800 mb-1">FinCEN Beneficial Ownership Rule</h3>
            <p className="text-sm text-gray-600 mb-3">
              Under the Customer Due Diligence (CDD) Rule, financial institutions must collect and verify 
              information about the beneficial owners of legal entity customers. A beneficial owner is any 
              individual who:
            </p>
            <ul className="text-sm text-gray-600 space-y-1.5">
              <li className="flex items-start gap-2">
                <span className="text-csb-green font-bold flex-shrink-0">•</span>
                Owns 25% or more equity interest in the legal entity, <strong>OR</strong>
              </li>
              <li className="flex items-start gap-2">
                <span className="text-csb-green font-bold flex-shrink-0">•</span>
                Controls, manages, or directs the legal entity (Control Person – at least one required)
              </li>
            </ul>
          </div>
        </div>
      </div>

      {/* Primary owner summary */}
      <div className="card">
        <h3 className="font-bold text-gray-700 mb-3 border-b pb-2">
          Primary Applicant (Already Captured)
        </h3>
        <div className="flex items-center gap-3 bg-csb-navy/5 rounded-lg p-3">
          <div className="w-10 h-10 rounded-full bg-csb-navy flex items-center justify-center text-white font-bold flex-shrink-0">
            {formData.firstName ? formData.firstName[0].toUpperCase() : '?'}
          </div>
          <div className="flex-1">
            <div className="font-semibold">{formData.firstName} {formData.lastName}</div>
            <div className="text-sm text-gray-500">{formData.title} · {formData.ownershipPct ? `${formData.ownershipPct}% ownership` : 'Ownership not set'}</div>
          </div>
          <div className="text-csb-green font-bold text-sm">✓ Added</div>
        </div>
      </div>

      {/* Ownership total */}
      <div className="card">
        <div className="flex items-center justify-between">
          <span className="font-semibold text-gray-700">Total Captured Ownership:</span>
          <span className={`text-lg font-bold ${totalPct > 100 ? 'text-red-600' : totalPct === 100 ? 'text-csb-green' : 'text-csb-navy'}`}>
            {totalPct.toFixed(0)}%
          </span>
        </div>
        {totalPct > 100 && (
          <p className="text-red-600 text-sm mt-1">Total exceeds 100%. Please check ownership percentages.</p>
        )}
        <div className="mt-2 h-2 bg-gray-200 rounded-full overflow-hidden">
          <div
            className={`h-full rounded-full transition-all ${totalPct > 100 ? 'bg-red-500' : 'bg-csb-green'}`}
            style={{ width: `${Math.min(totalPct, 100)}%` }}
          />
        </div>
      </div>

      {/* Additional beneficial owners */}
      {owners.length > 0 && (
        <div className="space-y-4">
          <h3 className="font-bold text-gray-700">Additional Beneficial Owners</h3>
          {owners.map((owner, i) => (
            <OwnerForm
              key={i}
              owner={owner}
              index={i}
              onUpdate={updateOwner}
              onRemove={removeOwner}
              errors={ownerErrors[i]}
            />
          ))}
        </div>
      )}

      <button
        type="button"
        className="w-full border-2 border-dashed border-gray-300 rounded-xl py-4 text-csb-navy font-semibold hover:border-csb-navy hover:bg-blue-50 transition-all flex items-center justify-center gap-2"
        onClick={addOwner}
      >
        <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
        </svg>
        Add Beneficial Owner (25%+ ownership)
      </button>

      {/* Certification */}
      <div className="card">
        <h3 className="font-bold text-gray-700 mb-3 border-b pb-2">Certification</h3>
        <label className={`flex items-start gap-3 cursor-pointer p-3 rounded-lg transition-colors ${
          formData.certifyBeneficialOwners ? 'bg-green-50' : 'hover:bg-gray-50'
        }`}>
          <input
            type="checkbox"
            className="mt-0.5 w-5 h-5 flex-shrink-0 accent-csb-navy"
            checked={formData.certifyBeneficialOwners}
            onChange={e => updateFormData({ certifyBeneficialOwners: e.target.checked })}
          />
          <span className="text-sm text-gray-700">
            I certify, to the best of my knowledge, that the information provided about each beneficial owner 
            is complete and correct, and I am authorized to provide this certification on behalf of the legal 
            entity listed in this application.
          </span>
        </label>
        {errors.certify && <p className="error-text mt-1">{errors.certify}</p>}
      </div>

      <div className="flex justify-between gap-3">
        <button className="btn-secondary" onClick={onBack}>← Back</button>
        <button className="btn-primary" onClick={handleNext}>Continue →</button>
      </div>
    </div>
  )
}
