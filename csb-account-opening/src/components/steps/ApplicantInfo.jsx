import { useState } from 'react'

const US_STATES = [
  'AL','AK','AZ','AR','CA','CO','CT','DE','FL','GA','HI','ID','IL','IN','IA',
  'KS','KY','LA','ME','MD','MA','MI','MN','MS','MO','MT','NE','NV','NH','NJ',
  'NM','NY','NC','ND','OH','OK','OR','PA','RI','SC','SD','TN','TX','UT','VT',
  'VA','WA','WV','WI','WY','DC'
]

const MAX_DOB = new Date(Date.now() - 18 * 365.25 * 24 * 60 * 60 * 1000).toISOString().split('T')[0]

const ID_TYPES = [
  { value: '', label: 'Select ID type...' },
  { value: 'drivers-license', label: "Driver's License" },
  { value: 'state-id', label: 'State-Issued Photo ID' },
  { value: 'passport', label: 'U.S. Passport' },
  { value: 'passport-card', label: 'U.S. Passport Card' },
  { value: 'military-id', label: 'Military ID' },
]

const TITLES = [
  { value: '', label: 'Select title...' },
  { value: 'owner', label: 'Owner' },
  { value: 'ceo', label: 'CEO / President' },
  { value: 'cfo', label: 'CFO / Treasurer' },
  { value: 'coo', label: 'COO' },
  { value: 'managing-member', label: 'Managing Member' },
  { value: 'partner', label: 'Partner' },
  { value: 'authorized-signer', label: 'Authorized Signer' },
]

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

export default function ApplicantInfo({ formData, updateFormData, onNext, onBack }) {
  const [errors, setErrors] = useState({})
  const [showSSN, setShowSSN] = useState(false)

  const set = (field) => (e) => updateFormData({ [field]: e.target.value })

  const formatPhone = (val) => {
    const d = val.replace(/\D/g, '').slice(0, 10)
    if (d.length >= 7) return `(${d.slice(0,3)}) ${d.slice(3,6)}-${d.slice(6)}`
    if (d.length >= 4) return `(${d.slice(0,3)}) ${d.slice(3)}`
    if (d.length) return `(${d}`
    return d
  }

  const formatSSN = (val) => {
    const d = val.replace(/\D/g, '').slice(0, 9)
    if (d.length >= 6) return `${d.slice(0,3)}-${d.slice(3,5)}-${d.slice(5)}`
    if (d.length >= 4) return `${d.slice(0,3)}-${d.slice(3)}`
    return d
  }

  const validate = () => {
    const e = {}
    if (!formData.firstName.trim()) e.firstName = 'First name is required.'
    if (!formData.lastName.trim()) e.lastName = 'Last name is required.'
    if (!formData.title) e.title = 'Please select your title/role.'
    if (!formData.email.trim()) e.email = 'Email address is required.'
    else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) e.email = 'Enter a valid email address.'
    if (!formData.phone.trim()) e.phone = 'Phone number is required.'
    if (!formData.dob) e.dob = 'Date of birth is required.'
    if (!formData.ssn.trim()) e.ssn = 'Social Security Number is required.'
    else if (!/^\d{3}-\d{2}-\d{4}$/.test(formData.ssn)) e.ssn = 'Enter a valid SSN (e.g., 123-45-6789).'
    if (!formData.address.trim()) e.address = 'Street address is required.'
    if (!formData.city.trim()) e.city = 'City is required.'
    if (!formData.state) e.state = 'State is required.'
    if (!formData.zip.trim()) e.zip = 'ZIP code is required.'
    else if (!/^\d{5}(-\d{4})?$/.test(formData.zip)) e.zip = 'Enter a valid ZIP code.'
    if (!formData.idType) e.idType = 'Please select an ID type.'
    if (!formData.idNumber.trim()) e.idNumber = 'ID number is required.'
    if (!formData.idExpiry) e.idExpiry = 'ID expiration date is required.'
    if (!formData.ownershipPct.trim()) e.ownershipPct = 'Ownership percentage is required.'
    else {
      const pct = parseFloat(formData.ownershipPct)
      if (isNaN(pct) || pct < 0 || pct > 100) e.ownershipPct = 'Enter a valid percentage (0–100).'
    }
    return e
  }

  const handleNext = () => {
    const e = validate()
    setErrors(e)
    if (Object.keys(e).length === 0) onNext()
  }

  return (
    <div className="space-y-6">
      <div className="card">
        <h2 className="section-header">Your Information</h2>
        <p className="text-gray-500 text-sm">
          As the primary applicant, we need to verify your identity and role in the business. 
          All fields marked with <span className="required-star">*</span> are required.
        </p>
      </div>

      {/* Personal Details */}
      <div className="card space-y-5">
        <h3 className="font-bold text-gray-700 border-b pb-2">Personal Details</h3>

        <div className="grid sm:grid-cols-2 gap-5">
          <Field label="First Name" required error={errors.firstName}>
            <input type="text" className="form-input" placeholder="Jane" value={formData.firstName} onChange={set('firstName')} />
          </Field>
          <Field label="Last Name" required error={errors.lastName}>
            <input type="text" className="form-input" placeholder="Smith" value={formData.lastName} onChange={set('lastName')} />
          </Field>
        </div>

        <div className="grid sm:grid-cols-2 gap-5">
          <Field label="Title / Role in Business" required error={errors.title}>
            <select className="form-input" value={formData.title} onChange={set('title')}>
              {TITLES.map(t => <option key={t.value} value={t.value}>{t.label}</option>)}
            </select>
          </Field>
          <Field label="Ownership Percentage" required error={errors.ownershipPct}
            hint="Enter your ownership stake in the business (0–100%)">
            <div className="relative">
              <input
                type="number"
                className="form-input pr-8"
                placeholder="e.g., 51"
                min="0"
                max="100"
                value={formData.ownershipPct}
                onChange={set('ownershipPct')}
              />
              <span className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 font-medium">%</span>
            </div>
          </Field>
        </div>

        <div className="grid sm:grid-cols-2 gap-5">
          <Field label="Email Address" required error={errors.email}>
            <input type="email" className="form-input" placeholder="jane@example.com" value={formData.email} onChange={set('email')} />
          </Field>
          <Field label="Mobile / Phone Number" required error={errors.phone}>
            <input
              type="tel"
              className="form-input"
              placeholder="(617) 555-1234"
              value={formData.phone}
              onChange={e => updateFormData({ phone: formatPhone(e.target.value) })}
            />
          </Field>
        </div>

        <div className="grid sm:grid-cols-2 gap-5">
          <Field label="Date of Birth" required error={errors.dob}>
            <input
              type="date"
              className="form-input"
              value={formData.dob}
              onChange={set('dob')}
              max={MAX_DOB}
            />
          </Field>
          <Field label="Social Security Number (SSN)" required error={errors.ssn}
            hint="Your SSN is encrypted and used only for identity verification.">
            <div className="relative">
              <input
                type={showSSN ? 'text' : 'password'}
                className="form-input pr-12"
                placeholder="XXX-XX-XXXX"
                value={formData.ssn}
                onChange={e => updateFormData({ ssn: formatSSN(e.target.value) })}
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
      </div>

      {/* Home Address */}
      <div className="card space-y-5">
        <h3 className="font-bold text-gray-700 border-b pb-2">Home Address</h3>
        <Field label="Street Address" required error={errors.address}>
          <input type="text" className="form-input" placeholder="456 Oak Avenue, Apt 2B" value={formData.address} onChange={set('address')} />
        </Field>
        <div className="grid sm:grid-cols-3 gap-5">
          <Field label="City" required error={errors.city}>
            <input type="text" className="form-input" placeholder="Cambridge" value={formData.city} onChange={set('city')} />
          </Field>
          <Field label="State" required error={errors.state}>
            <select className="form-input" value={formData.state} onChange={set('state')}>
              <option value="">State</option>
              {US_STATES.map(s => <option key={s} value={s}>{s}</option>)}
            </select>
          </Field>
          <Field label="ZIP Code" required error={errors.zip}>
            <input type="text" className="form-input" placeholder="02138" value={formData.zip} onChange={set('zip')} maxLength={10} />
          </Field>
        </div>
      </div>

      {/* Government ID */}
      <div className="card space-y-5">
        <h3 className="font-bold text-gray-700 border-b pb-2">Government-Issued Identification</h3>
        <div className="info-box">
          Federal law requires us to verify the identity of all persons associated with the account.
          Please provide a valid, unexpired government-issued photo ID.
        </div>

        <div className="grid sm:grid-cols-2 gap-5">
          <Field label="ID Type" required error={errors.idType}>
            <select className="form-input" value={formData.idType} onChange={set('idType')}>
              {ID_TYPES.map(t => <option key={t.value} value={t.value}>{t.label}</option>)}
            </select>
          </Field>
          {(formData.idType === 'drivers-license' || formData.idType === 'state-id') && (
            <Field label="Issuing State" error={errors.idState}>
              <select className="form-input" value={formData.idState} onChange={set('idState')}>
                <option value="">Select state...</option>
                {US_STATES.map(s => <option key={s} value={s}>{s}</option>)}
              </select>
            </Field>
          )}
        </div>

        <div className="grid sm:grid-cols-2 gap-5">
          <Field label="ID Number" required error={errors.idNumber}>
            <input type="text" className="form-input" placeholder="ID number" value={formData.idNumber} onChange={set('idNumber')} />
          </Field>
          <Field label="Expiration Date" required error={errors.idExpiry}>
            <input
              type="date"
              className="form-input"
              value={formData.idExpiry}
              onChange={set('idExpiry')}
              min={new Date().toISOString().split('T')[0]}
            />
          </Field>
        </div>
      </div>

      <div className="flex justify-between gap-3">
        <button className="btn-secondary" onClick={onBack}>← Back</button>
        <button className="btn-primary" onClick={handleNext}>Continue →</button>
      </div>
    </div>
  )
}
